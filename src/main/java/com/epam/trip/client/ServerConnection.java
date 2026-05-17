package com.epam.trip.client;

import com.epam.trip.protocol.Command;
import com.epam.trip.protocol.Request;
import com.epam.trip.protocol.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * Manages the TCP connection to the server and sends/receives JSON messages.
 */
public class ServerConnection implements Closeable {
    private static final Gson GSON = new GsonBuilder().create();

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private String token;

    public ServerConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        this.in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Read server welcome message
        this.in.readLine();
    }

    public Response send(Command command, Map<String, String> params) throws IOException {
        Request req = new Request(command, token, params);
        out.println(GSON.toJson(req));
        String line = in.readLine();
        if (line == null) throw new IOException("Server closed connection");
        return GSON.fromJson(line, Response.class);
    }

    public Response send(Command command) throws IOException {
        return send(command, Map.of());
    }

    public void setToken(String token) { this.token = token; }
    public String getToken() { return token; }
    public boolean hasToken() { return token != null; }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
