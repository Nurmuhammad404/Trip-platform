package com.epam.trip.server;

import com.epam.trip.api.AviationstackClient;
import com.epam.trip.api.FlightApiService;
import com.epam.trip.auth.AuthService;
import com.epam.trip.dao.db.DatabaseManager;
import com.epam.trip.dao.db.JdbcBookingDao;
import com.epam.trip.dao.db.JdbcCarDao;
import com.epam.trip.dao.db.JdbcHotelDao;
import com.epam.trip.dao.db.JdbcPlaceDao;
import com.epam.trip.dao.db.JdbcTaxiDao;
import com.epam.trip.dao.db.JdbcTourDao;
import com.epam.trip.dao.db.JdbcUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TCP server — accepts one client per thread.
 * Flights are served live from Aviationstack API; all other data uses SQLite.
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final int port;
    private final ExecutorService threadPool;
    private final AuthService authService;
    private final DatabaseManager dbManager;
    private final AtomicInteger clientCount = new AtomicInteger(0);
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    private final JdbcUserDao    userDao;
    private final FlightApiService flightService;
    private final JdbcHotelDao   hotelDao;
    private final JdbcCarDao     carDao;
    private final JdbcPlaceDao   placeDao;
    private final JdbcTourDao    tourDao;
    private final JdbcTaxiDao    taxiDao;
    private final JdbcBookingDao bookingDao;

    public Server(int port, DatabaseManager dbManager, AuthService authService,
                  String aviationApiKey, String aviationBaseUrl,
                  int apiTimeout, int apiRetry) {
        this.port = port;
        this.dbManager = dbManager;
        this.authService = authService;
        this.threadPool = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        this.userDao    = new JdbcUserDao(dbManager);
        this.hotelDao   = new JdbcHotelDao(dbManager);
        this.carDao     = new JdbcCarDao(dbManager);
        this.placeDao   = new JdbcPlaceDao(dbManager);
        this.tourDao    = new JdbcTourDao(dbManager);
        this.taxiDao    = new JdbcTaxiDao(dbManager);
        this.bookingDao = new JdbcBookingDao(dbManager);

        AviationstackClient aviationClient =
                new AviationstackClient(aviationApiKey, aviationBaseUrl, apiTimeout, apiRetry);
        this.flightService = new FlightApiService(aviationClient);
        log.info("Aviationstack API client initialised");
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        log.info("Trip Platform Server started on port {}", port);
        System.out.println("[SERVER] Listening on port " + port + " — waiting for clients...");

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        while (running) {
            try {
                Socket client = serverSocket.accept();
                int id = clientCount.incrementAndGet();
                log.info("Client #{} connected from {}", id, client.getRemoteSocketAddress());
                threadPool.submit(new ClientHandler(
                        client, id, authService,
                        userDao, flightService, hotelDao, carDao,
                        placeDao, tourDao, taxiDao, bookingDao));
            } catch (IOException e) {
                if (running) log.error("Accept error", e);
            }
        }
    }

    public void stop() {
        running = false;
        threadPool.shutdown();
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        dbManager.shutdown();
        log.info("Server stopped");
    }
}
