package com.epam.trip.service.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingServiceImpl
 * Tests all CRUD operations and business logic methods
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private GenericDao<Booking> mockBookingDao;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(mockBookingDao);
    }

    @Test
    void testFindById_ValidId_ReturnsBooking() {
        // Arrange
        Long bookingId = 1L;
        Booking expectedBooking = createSampleBooking(bookingId);
        when(mockBookingDao.findById(bookingId)).thenReturn(expectedBooking);

        // Act
        Booking actualBooking = bookingService.findById(bookingId);

        // Assert
        assertNotNull(actualBooking);
        assertEquals(expectedBooking.getId(), actualBooking.getId());
        verify(mockBookingDao, times(1)).findById(bookingId);
    }

    @Test
    void testFindById_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.findById(null));
        assertEquals("Invalid booking ID", exception.getMessage());
        verify(mockBookingDao, never()).findById(any());
    }

    @Test
    void testFindById_NegativeId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.findById(-1L));
        assertEquals("Invalid booking ID", exception.getMessage());
        verify(mockBookingDao, never()).findById(any());
    }

    @Test
    void testFindAll_ReturnsAllBookings() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(
                createSampleBooking(1L),
                createSampleBooking(2L),
                createSampleBooking(3L));
        when(mockBookingDao.findAll()).thenReturn(expectedBookings);

        // Act
        List<Booking> actualBookings = bookingService.findAll();

        // Assert
        assertNotNull(actualBookings);
        assertEquals(3, actualBookings.size());
        verify(mockBookingDao, times(1)).findAll();
    }

    @Test
    void testSave_ValidBooking_SetsPendingStatusAndSaves() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setStatus(null); // Ensure status is null initially

        // Act
        bookingService.save(booking);

        // Assert
        assertEquals("PENDING", booking.getStatus());
        verify(mockBookingDao, times(1)).save(booking);
    }

    @Test
    void testSave_BookingWithExistingStatus_KeepsStatus() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setStatus("CONFIRMED");

        // Act
        bookingService.save(booking);

        // Assert
        assertEquals("CONFIRMED", booking.getStatus());
        verify(mockBookingDao, times(1)).save(booking);
    }

    @Test
    void testSave_NullBooking_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.save(null));
        assertEquals("Booking cannot be null", exception.getMessage());
        verify(mockBookingDao, never()).save(any());
    }

    @Test
    void testSave_NullUserId_ThrowsException() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setUserId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.save(booking));
        assertEquals("User ID is required", exception.getMessage());
    }

    @Test
    void testSave_NullServiceType_ThrowsException() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setServiceType(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.save(booking));
        assertEquals("Service type is required", exception.getMessage());
    }

    @Test
    void testSave_EmptyServiceType_ThrowsException() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setServiceType("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.save(booking));
        assertEquals("Service type is required", exception.getMessage());
    }

    @Test
    void testSave_NullServiceId_ThrowsException() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setServiceId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.save(booking));
        assertEquals("Service ID is required", exception.getMessage());
    }

    @Test
    void testSave_NegativePrice_ThrowsException() {
        // Arrange
        Booking booking = createSampleBooking(null);
        booking.setTotalPrice(-100.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.save(booking));
        assertEquals("Total price cannot be negative", exception.getMessage());
    }

    @Test
    void testUpdate_ValidBooking_UpdatesBooking() {
        // Arrange
        Booking booking = createSampleBooking(1L);

        // Act
        bookingService.update(booking);

        // Assert
        verify(mockBookingDao, times(1)).update(booking);
    }

    @Test
    void testUpdate_NullId_ThrowsException() {
        // Arrange
        Booking booking = createSampleBooking(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.update(booking));
        assertEquals("Booking ID cannot be null for update", exception.getMessage());
        verify(mockBookingDao, never()).update(any());
    }

    @Test
    void testDelete_ValidId_DeletesBooking() {
        // Arrange
        Long bookingId = 1L;

        // Act
        bookingService.delete(bookingId);

        // Assert
        verify(mockBookingDao, times(1)).delete(bookingId);
    }

    @Test
    void testDelete_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.delete(null));
        assertEquals("Invalid booking ID", exception.getMessage());
        verify(mockBookingDao, never()).delete(any());
    }

    @Test
    void testFindByUserId_ReturnsFilteredBookings() {
        // Arrange
        Long userId = 1L;
        List<Booking> allBookings = Arrays.asList(
                createSampleBooking(1L, userId, "TOUR", 101L),
                createSampleBooking(2L, 2L, "HOTEL", 201L),
                createSampleBooking(3L, userId, "FLIGHT", 301L));
        when(mockBookingDao.findAll()).thenReturn(allBookings);

        // Act
        List<Booking> result = bookingService.findByUserId(userId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getUserId().equals(userId)));
    }

    @Test
    void testFindByStatus_ReturnsFilteredBookings() {
        // Arrange
        String status = "PENDING";
        List<Booking> allBookings = Arrays.asList(
                createBookingWithStatus(1L, "PENDING"),
                createBookingWithStatus(2L, "CONFIRMED"),
                createBookingWithStatus(3L, "PENDING"));
        when(mockBookingDao.findAll()).thenReturn(allBookings);

        // Act
        List<Booking> result = bookingService.findByStatus(status);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getStatus().equalsIgnoreCase(status)));
    }

    @Test
    void testFindByServiceType_ReturnsFilteredBookings() {
        // Arrange
        String serviceType = "TOUR";
        List<Booking> allBookings = Arrays.asList(
                createBookingWithServiceType(1L, "TOUR"),
                createBookingWithServiceType(2L, "HOTEL"),
                createBookingWithServiceType(3L, "tour") // different case
        );
        when(mockBookingDao.findAll()).thenReturn(allBookings);

        // Act
        List<Booking> result = bookingService.findByServiceType(serviceType);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> b.getServiceType().equalsIgnoreCase(serviceType)));
    }

    @Test
    void testConfirmBooking_PendingBooking_ReturnsTrue() {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBookingWithStatus(bookingId, "PENDING");
        when(mockBookingDao.findById(bookingId)).thenReturn(booking);

        // Act
        boolean result = bookingService.confirmBooking(bookingId);

        // Assert
        assertTrue(result);
        assertEquals("CONFIRMED", booking.getStatus());
        verify(mockBookingDao, times(1)).update(booking);
    }

    @Test
    void testConfirmBooking_AlreadyConfirmed_ReturnsFalse() {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBookingWithStatus(bookingId, "CONFIRMED");
        when(mockBookingDao.findById(bookingId)).thenReturn(booking);

        // Act
        boolean result = bookingService.confirmBooking(bookingId);

        // Assert
        assertFalse(result);
        verify(mockBookingDao, never()).update(any());
    }

    @Test
    void testConfirmBooking_NullBooking_ReturnsFalse() {
        // Arrange
        Long bookingId = 1L;
        when(mockBookingDao.findById(bookingId)).thenReturn(null);

        // Act
        boolean result = bookingService.confirmBooking(bookingId);

        // Assert
        assertFalse(result);
        verify(mockBookingDao, never()).update(any());
    }

    @Test
    void testCancelBooking_ExistingBooking_ReturnsTrue() {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBookingWithStatus(bookingId, "CONFIRMED");
        when(mockBookingDao.findById(bookingId)).thenReturn(booking);

        // Act
        boolean result = bookingService.cancelBooking(bookingId);

        // Assert
        assertTrue(result);
        assertEquals("CANCELLED", booking.getStatus());
        verify(mockBookingDao, times(1)).update(booking);
    }

    @Test
    void testCancelBooking_AlreadyCancelled_ReturnsFalse() {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBookingWithStatus(bookingId, "CANCELLED");
        when(mockBookingDao.findById(bookingId)).thenReturn(booking);

        // Act
        boolean result = bookingService.cancelBooking(bookingId);

        // Assert
        assertFalse(result);
        verify(mockBookingDao, never()).update(any());
    }

    // Helper methods for creating test data

    private Booking createSampleBooking(Long id) {
        return new Booking(id, 1L, "TOUR", 101L, "2026-01-15",
                "PENDING", 500.0, "John Doe", "john@example.com");
    }

    private Booking createSampleBooking(Long id, Long userId, String serviceType, Long serviceId) {
        return new Booking(id, userId, serviceType, serviceId, "2026-01-15",
                "PENDING", 500.0, "John Doe", "john@example.com");
    }

    private Booking createBookingWithStatus(Long id, String status) {
        Booking booking = createSampleBooking(id);
        booking.setStatus(status);
        return booking;
    }

    private Booking createBookingWithServiceType(Long id, String serviceType) {
        Booking booking = createSampleBooking(id);
        booking.setServiceType(serviceType);
        return booking;
    }
}
