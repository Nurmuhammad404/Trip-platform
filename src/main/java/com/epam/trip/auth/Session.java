package com.epam.trip.auth;

import java.time.Instant;

public class Session {
    private final String token;
    private final Long userId;
    private final String username;
    private final Role role;
    private final Instant createdAt;

    public Session(String token, Long userId, String username, Role role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.createdAt = Instant.now();
    }

    /** Anonymous visitor session — no user identity. */
    public static Session visitor() {
        return new Session(null, null, "visitor", Role.VISITOR);
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isAuthenticated() { return userId != null; }

    public boolean hasRole(Role required) {
        return role.ordinal() >= required.ordinal();
    }
}
