package com.epam.trip.dao.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that owns the ConnectionPool and creates the DB schema on first run.
 */
public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;

    private final ConnectionPool pool;

    private DatabaseManager(String dbPath, int poolSize) {
        String url = "jdbc:sqlite:" + dbPath;
        this.pool = new ConnectionPool(url, poolSize);
        initSchema();
        log.info("DatabaseManager initialised — {}", url);
    }

    public static synchronized DatabaseManager getInstance(String dbPath, int poolSize) {
        if (instance == null) {
            instance = new DatabaseManager(dbPath, poolSize);
        }
        return instance;
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) throw new IllegalStateException("DatabaseManager not yet initialised");
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return pool.acquire();
    }

    public void releaseConnection(Connection conn) {
        pool.release(conn);
    }

    public void shutdown() {
        pool.closeAll();
        log.info("DatabaseManager shut down");
    }

    private void initSchema() {
        String[] ddl = {
            """
            CREATE TABLE IF NOT EXISTS users (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                username    TEXT NOT NULL UNIQUE,
                email       TEXT NOT NULL UNIQUE,
                password    TEXT NOT NULL,
                full_name   TEXT,
                phone       TEXT,
                role        TEXT NOT NULL DEFAULT 'USER'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS hotels (
                id               INTEGER PRIMARY KEY AUTOINCREMENT,
                name             TEXT NOT NULL,
                city             TEXT NOT NULL,
                address          TEXT,
                star_rating      INTEGER NOT NULL DEFAULT 3,
                price_per_night  REAL NOT NULL DEFAULT 0,
                available_rooms  INTEGER NOT NULL DEFAULT 0,
                amenities        TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS cars (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                brand         TEXT NOT NULL,
                model         TEXT NOT NULL,
                type          TEXT,
                price_per_day REAL NOT NULL DEFAULT 0,
                available     INTEGER NOT NULL DEFAULT 1,
                location      TEXT,
                seats         INTEGER NOT NULL DEFAULT 5,
                transmission  TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS places (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT NOT NULL,
                city        TEXT NOT NULL,
                country     TEXT,
                description TEXT,
                category    TEXT,
                rating      REAL NOT NULL DEFAULT 0,
                entry_fee   REAL NOT NULL DEFAULT 0
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS tours (
                id             INTEGER PRIMARY KEY AUTOINCREMENT,
                name           TEXT NOT NULL,
                destination    TEXT NOT NULL,
                duration       INTEGER NOT NULL DEFAULT 1,
                price          REAL NOT NULL DEFAULT 0,
                description    TEXT,
                max_group_size INTEGER NOT NULL DEFAULT 10,
                guide          TEXT,
                schedule       TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS taxis (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                driver_name   TEXT NOT NULL,
                vehicle_type  TEXT,
                license_plate TEXT NOT NULL UNIQUE,
                city          TEXT NOT NULL,
                price_per_km  REAL NOT NULL DEFAULT 0,
                available     INTEGER NOT NULL DEFAULT 1,
                phone         TEXT,
                rating        REAL NOT NULL DEFAULT 0
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS bookings (
                id             INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id        INTEGER NOT NULL,
                service_type   TEXT NOT NULL,
                service_id     INTEGER NOT NULL,
                booking_date   TEXT,
                status         TEXT NOT NULL DEFAULT 'PENDING',
                total_price    REAL NOT NULL DEFAULT 0,
                customer_name  TEXT,
                customer_email TEXT
            )
            """
        };

        Connection conn = null;
        try {
            conn = pool.acquire();
            try (Statement stmt = conn.createStatement()) {
                for (String sql : ddl) {
                    stmt.execute(sql);
                }
            }
            log.info("Database schema initialised");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialise DB schema", e);
        } finally {
            pool.release(conn);
        }
    }
}
