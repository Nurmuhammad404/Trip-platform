package com.epam.trip;

import com.epam.trip.auth.AuthServiceImpl;
import com.epam.trip.auth.Role;
import com.epam.trip.dao.db.DatabaseManager;
import com.epam.trip.dao.db.JdbcUserDao;
import com.epam.trip.server.Server;
import com.epam.trip.startup.DataSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Server entry point. Reads app.properties, boots DatabaseManager, seeds data, and starts Server.
 * Run client: mvn exec:java -Dexec.mainClass=com.epam.trip.client.ClientApp
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        Properties props = loadProperties();

        String dbPath   = props.getProperty("db.path", "trip-platform.db");
        int    poolSize = Integer.parseInt(props.getProperty("db.pool.size", "5"));
        int    port     = Integer.parseInt(props.getProperty("server.port", "7777"));

        String aviationKey     = props.getProperty("api.aviationstack.key", "");
        String aviationUrl     = props.getProperty("api.aviationstack.url", "http://api.aviationstack.com/v1");
        int    apiTimeout      = Integer.parseInt(props.getProperty("api.timeout.seconds", "30"));
        int    apiRetry        = Integer.parseInt(props.getProperty("api.retry.count", "2"));

        DatabaseManager dbManager = DatabaseManager.getInstance(dbPath, poolSize);

        new DataSeeder(dbManager).seedIfEmpty();

        JdbcUserDao userDao = new JdbcUserDao(dbManager);
        AuthServiceImpl authService = new AuthServiceImpl(userDao);

        ensureAdminExists(userDao, authService);

        Server server = new Server(port, dbManager, authService,
                aviationKey, aviationUrl, apiTimeout, apiRetry);
        server.start();
    }

    private static void ensureAdminExists(JdbcUserDao userDao, AuthServiceImpl authService) {
        boolean hasAdmin = userDao.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.ADMIN);
        if (!hasAdmin) {
            authService.register("admin", "admin@trip.com", "admin123", "Administrator", "");
            userDao.findByUsername("admin").ifPresent(u -> {
                u.setRole(Role.ADMIN);
                userDao.update(u);
            });
            System.out.println("[SERVER] Admin account created — username: admin  password: admin123");
            log.info("Default admin created");
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (in != null) props.load(in);
        }
        return props;
    }
}
