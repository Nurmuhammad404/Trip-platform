package com.epam.trip.dao.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Simple fixed-size connection pool for SQLite.
 * Blocks callers when all connections are busy (up to timeoutMs).
 */
public class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    private final Deque<Connection> free;
    private final int maxSize;
    private final String jdbcUrl;
    private int created = 0;

    public ConnectionPool(String jdbcUrl, int maxSize) {
        this.jdbcUrl = jdbcUrl;
        this.maxSize = maxSize;
        this.free = new ArrayDeque<>(maxSize);
    }

    public synchronized Connection acquire() throws SQLException {
        if (!free.isEmpty()) {
            return free.poll();
        }
        if (created < maxSize) {
            created++;
            log.debug("Opening new DB connection #{}", created);
            return DriverManager.getConnection(jdbcUrl);
        }
        // Wait for a connection to be released
        long deadline = System.currentTimeMillis() + 5000;
        while (free.isEmpty()) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) throw new SQLException("Connection pool timeout");
            try {
                wait(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("Interrupted while waiting for connection");
            }
        }
        return free.poll();
    }

    public synchronized void release(Connection conn) {
        if (conn != null) {
            free.push(conn);
            notifyAll();
        }
    }

    public synchronized void closeAll() {
        for (Connection conn : free) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
        free.clear();
        created = 0;
    }
}
