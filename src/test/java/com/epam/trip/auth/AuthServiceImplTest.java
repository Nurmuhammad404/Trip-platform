package com.epam.trip.auth;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.User;
import com.epam.trip.exception.ServiceException;
import com.epam.trip.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock GenericDao<User> userDao;
    AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userDao);
    }

    @Test
    void login_validCredentials_returnsSession() {
        User user = buildUser("alice", AuthServiceImpl.hash("pass123"), Role.USER);
        when(userDao.findAll()).thenReturn(List.of(user));

        Session session = authService.login("alice", "pass123");

        assertNotNull(session);
        assertEquals("alice", session.getUsername());
        assertEquals(Role.USER, session.getRole());
        assertTrue(session.isAuthenticated());
    }

    @Test
    void login_wrongPassword_throwsServiceException() {
        User user = buildUser("alice", AuthServiceImpl.hash("correct"), Role.USER);
        when(userDao.findAll()).thenReturn(List.of(user));

        assertThrows(ServiceException.class, () -> authService.login("alice", "wrong"));
    }

    @Test
    void login_unknownUser_throwsServiceException() {
        when(userDao.findAll()).thenReturn(List.of());

        assertThrows(ServiceException.class, () -> authService.login("nobody", "pass"));
    }

    @Test
    void login_nullUsername_throwsValidationException() {
        assertThrows(ValidationException.class, () -> authService.login(null, "pass"));
    }

    @Test
    void register_newUser_createsSessionWithUserRole() {
        when(userDao.findAll()).thenReturn(List.of());
        when(userDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Session session = authService.register("bob", "bob@test.com", "secret1", "Bob", "");

        assertEquals("bob", session.getUsername());
        assertEquals(Role.USER, session.getRole());
        verify(userDao).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsServiceException() {
        User existing = buildUser("bob", "hash", Role.USER);
        existing.setEmail("bob@test.com");
        when(userDao.findAll()).thenReturn(List.of(existing));

        assertThrows(ServiceException.class,
                () -> authService.register("bob", "other@test.com", "pass123", "", ""));
    }

    @Test
    void logout_invalidatesSession() {
        User user = buildUser("alice", AuthServiceImpl.hash("pass"), Role.USER);
        when(userDao.findAll()).thenReturn(List.of(user));

        Session session = authService.login("alice", "pass");
        String token = session.getToken();
        authService.logout(token);

        Session retrieved = SessionManager.getInstance().get(token);
        assertFalse(retrieved.isAuthenticated());
    }

    private User buildUser(String username, String password, Role role) {
        User u = new User();
        u.setId(1L);
        u.setUsername(username);
        u.setEmail(username + "@test.com");
        u.setPassword(password);
        u.setRole(role);
        return u;
    }
}
