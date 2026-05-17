package com.epam.trip.auth;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() { return INSTANCE; }

    public Session create(Long userId, String username, Role role) {
        String token = UUID.randomUUID().toString();
        Session session = new Session(token, userId, username, role);
        sessions.put(token, session);
        return session;
    }

    public Session get(String token) {
        if (token == null || token.isBlank()) return Session.visitor();
        return sessions.getOrDefault(token, Session.visitor());
    }

    public void invalidate(String token) {
        if (token != null) sessions.remove(token);
    }

    public int activeCount() { return sessions.size(); }
}
