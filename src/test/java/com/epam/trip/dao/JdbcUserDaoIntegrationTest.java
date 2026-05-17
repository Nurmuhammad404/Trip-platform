package com.epam.trip.dao;

import com.epam.trip.auth.Role;
import com.epam.trip.dao.db.DatabaseManager;
import com.epam.trip.dao.db.JdbcUserDao;
import com.epam.trip.entity.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test — uses a real in-memory SQLite database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcUserDaoIntegrationTest {

    static DatabaseManager db;
    static JdbcUserDao dao;

    @BeforeAll
    static void setUpDb() {
        db = DatabaseManager.getInstance(":memory:", 2);
        dao = new JdbcUserDao(db);
    }

    @AfterAll
    static void tearDown() {
        db.shutdown();
    }

    @Test
    @Order(1)
    void save_newUser_assignsId() {
        User u = user("alice", "alice@test.com", Role.USER);
        dao.save(u);
        assertNotNull(u.getId());
        assertTrue(u.getId() > 0);
    }

    @Test
    @Order(2)
    void findAll_returnsAllSaved() {
        dao.save(user("bob", "bob@test.com", Role.USER));
        List<User> all = dao.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    @Order(3)
    void findByUsername_existingUser_found() {
        Optional<User> found = dao.findByUsername("alice");
        assertTrue(found.isPresent());
        assertEquals("alice", found.get().getUsername());
    }

    @Test
    @Order(4)
    void findByUsername_unknown_empty() {
        Optional<User> found = dao.findByUsername("nobody");
        assertFalse(found.isPresent());
    }

    @Test
    @Order(5)
    void update_changesRole() {
        Optional<User> opt = dao.findByUsername("alice");
        assertTrue(opt.isPresent());
        User u = opt.get();
        u.setRole(Role.ADMIN);
        dao.update(u);

        User updated = dao.findById(u.getId());
        assertEquals(Role.ADMIN, updated.getRole());
    }

    @Test
    @Order(6)
    void delete_removesUser() {
        Optional<User> opt = dao.findByUsername("bob");
        assertTrue(opt.isPresent());
        long id = opt.get().getId();
        dao.delete(id);
        assertNull(dao.findById(id));
    }

    private User user(String username, String email, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword("hashed-pw");
        u.setFullName("Test User");
        u.setRole(role);
        return u;
    }
}
