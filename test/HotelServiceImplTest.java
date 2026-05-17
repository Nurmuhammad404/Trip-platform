package com.epam.trip.service.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Hotel;
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
 * Unit tests for HotelServiceImpl
 * Tests all CRUD operations, search functionality, and room booking logic
 */
@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private GenericDao<Hotel> mockHotelDao;

    private HotelServiceImpl hotelService;

    @BeforeEach
    void setUp() {
        hotelService = new HotelServiceImpl(mockHotelDao);
    }

    @Test
    void testFindById_ValidId_ReturnsHotel() {
        // Arrange
        Long hotelId = 1L;
        Hotel expectedHotel = createSampleHotel(hotelId);
        when(mockHotelDao.findById(hotelId)).thenReturn(expectedHotel);

        // Act
        Hotel actualHotel = hotelService.findById(hotelId);

        // Assert
        assertNotNull(actualHotel);
        assertEquals(expectedHotel.getId(), actualHotel.getId());
        assertEquals(expectedHotel.getName(), actualHotel.getName());
        verify(mockHotelDao, times(1)).findById(hotelId);
    }

    @Test
    void testFindById_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.findById(null));
        assertEquals("Invalid hotel ID", exception.getMessage());
        verify(mockHotelDao, never()).findById(any());
    }

    @Test
    void testFindById_NegativeId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.findById(-1L));
        assertEquals("Invalid hotel ID", exception.getMessage());
    }

    @Test
    void testFindAll_ReturnsAllHotels() {
        // Arrange
        List<Hotel> expectedHotels = Arrays.asList(
                createSampleHotel(1L),
                createSampleHotel(2L),
                createSampleHotel(3L));
        when(mockHotelDao.findAll()).thenReturn(expectedHotels);

        // Act
        List<Hotel> actualHotels = hotelService.findAll();

        // Assert
        assertNotNull(actualHotels);
        assertEquals(3, actualHotels.size());
        verify(mockHotelDao, times(1)).findAll();
    }

    @Test
    void testSave_ValidHotel_SavesHotel() {
        // Arrange
        Hotel hotel = createSampleHotel(null);

        // Act
        hotelService.save(hotel);

        // Assert
        verify(mockHotelDao, times(1)).save(hotel);
    }

    @Test
    void testSave_NullHotel_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(null));
        assertEquals("Hotel cannot be null", exception.getMessage());
        verify(mockHotelDao, never()).save(any());
    }

    @Test
    void testSave_NullName_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);
        hotel.setName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(hotel));
        assertEquals("Hotel name is required", exception.getMessage());
    }

    @Test
    void testSave_EmptyName_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);
        hotel.setName("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(hotel));
        assertEquals("Hotel name is required", exception.getMessage());
    }

    @Test
    void testSave_NullCity_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);
        hotel.setCity(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(hotel));
        assertEquals("City is required", exception.getMessage());
    }

    @Test
    void testSave_NegativePrice_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);
        hotel.setPricePerNight(-100.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(hotel));
        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    void testSave_InvalidStarRating_Negative_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);
        hotel.setStarRating(-1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(hotel));
        assertEquals("Star rating must be between 0 and 5", exception.getMessage());
    }

    @Test
    void testSave_InvalidStarRating_TooHigh_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);
        hotel.setStarRating(6);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.save(hotel));
        assertEquals("Star rating must be between 0 and 5", exception.getMessage());
    }

    @Test
    void testUpdate_ValidHotel_UpdatesHotel() {
        // Arrange
        Hotel hotel = createSampleHotel(1L);

        // Act
        hotelService.update(hotel);

        // Assert
        verify(mockHotelDao, times(1)).update(hotel);
    }

    @Test
    void testUpdate_NullId_ThrowsException() {
        // Arrange
        Hotel hotel = createSampleHotel(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.update(hotel));
        assertEquals("Hotel ID cannot be null for update", exception.getMessage());
        verify(mockHotelDao, never()).update(any());
    }

    @Test
    void testDelete_ValidId_DeletesHotel() {
        // Arrange
        Long hotelId = 1L;

        // Act
        hotelService.delete(hotelId);

        // Assert
        verify(mockHotelDao, times(1)).delete(hotelId);
    }

    @Test
    void testDelete_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.delete(null));
        assertEquals("Invalid hotel ID", exception.getMessage());
    }

    @Test
    void testSearchByCity_FindsMatchingHotels() {
        // Arrange
        List<Hotel> allHotels = Arrays.asList(
                createHotelInCity(1L, "Paris"),
                createHotelInCity(2L, "Rome"),
                createHotelInCity(3L, "Paris"));
        when(mockHotelDao.findAll()).thenReturn(allHotels);

        // Act
        List<Hotel> result = hotelService.searchByCity("Paris");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(h -> h.getCity().equalsIgnoreCase("Paris")));
    }

    @Test
    void testSearchByCity_CaseInsensitive() {
        // Arrange
        List<Hotel> allHotels = Arrays.asList(
                createHotelInCity(1L, "PARIS"),
                createHotelInCity(2L, "London"));
        when(mockHotelDao.findAll()).thenReturn(allHotels);

        // Act
        List<Hotel> result = hotelService.searchByCity("paris");

        // Assert
        assertEquals(1, result.size());
        assertEquals("PARIS", result.get(0).getCity());
    }

    @Test
    void testSearchByPriceRange_FindsHotelsInRange() {
        // Arrange
        List<Hotel> allHotels = Arrays.asList(
                createHotelWithPrice(1L, 100.0),
                createHotelWithPrice(2L, 250.0),
                createHotelWithPrice(3L, 150.0),
                createHotelWithPrice(4L, 350.0));
        when(mockHotelDao.findAll()).thenReturn(allHotels);

        // Act
        List<Hotel> result = hotelService.searchByPriceRange(100.0, 250.0);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(h -> h.getPricePerNight() >= 100.0 && h.getPricePerNight() <= 250.0));
    }

    @Test
    void testSearchByStarRating_FindsHotelsWithMinRating() {
        // Arrange
        List<Hotel> allHotels = Arrays.asList(
                createHotelWithStarRating(1L, 3),
                createHotelWithStarRating(2L, 5),
                createHotelWithStarRating(3L, 4),
                createHotelWithStarRating(4L, 2));
        when(mockHotelDao.findAll()).thenReturn(allHotels);

        // Act
        List<Hotel> result = hotelService.searchByStarRating(4);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(h -> h.getStarRating() >= 4));
    }

    @Test
    void testGetAvailableHotels_ReturnsOnlyAvailableRooms() {
        // Arrange
        List<Hotel> allHotels = Arrays.asList(
                createHotelWithAvailableRooms(1L, 5),
                createHotelWithAvailableRooms(2L, 0),
                createHotelWithAvailableRooms(3L, 10));
        when(mockHotelDao.findAll()).thenReturn(allHotels);

        // Act
        List<Hotel> result = hotelService.getAvailableHotels();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(h -> h.getAvailableRooms() > 0));
    }

    @Test
    void testBookRoom_AvailableRooms_ReturnsTrue() {
        // Arrange
        Long hotelId = 1L;
        Hotel hotel = createHotelWithAvailableRooms(hotelId, 5);
        when(mockHotelDao.findById(hotelId)).thenReturn(hotel);

        // Act
        boolean result = hotelService.bookRoom(hotelId);

        // Assert
        assertTrue(result);
        assertEquals(4, hotel.getAvailableRooms());
        verify(mockHotelDao, times(1)).update(hotel);
    }

    @Test
    void testBookRoom_NoAvailableRooms_ReturnsFalse() {
        // Arrange
        Long hotelId = 1L;
        Hotel hotel = createHotelWithAvailableRooms(hotelId, 0);
        when(mockHotelDao.findById(hotelId)).thenReturn(hotel);

        // Act
        boolean result = hotelService.bookRoom(hotelId);

        // Assert
        assertFalse(result);
        assertEquals(0, hotel.getAvailableRooms());
        verify(mockHotelDao, never()).update(any());
    }

    @Test
    void testBookRoom_NullHotel_ReturnsFalse() {
        // Arrange
        Long hotelId = 1L;
        when(mockHotelDao.findById(hotelId)).thenReturn(null);

        // Act
        boolean result = hotelService.bookRoom(hotelId);

        // Assert
        assertFalse(result);
        verify(mockHotelDao, never()).update(any());
    }

    @Test
    void testBookRoom_LastAvailableRoom() {
        // Arrange
        Long hotelId = 1L;
        Hotel hotel = createHotelWithAvailableRooms(hotelId, 1);
        when(mockHotelDao.findById(hotelId)).thenReturn(hotel);

        // Act
        boolean result = hotelService.bookRoom(hotelId);

        // Assert
        assertTrue(result);
        assertEquals(0, hotel.getAvailableRooms());
        verify(mockHotelDao, times(1)).update(hotel);
    }

    // Helper methods for creating test data

    private Hotel createSampleHotel(Long id) {
        return new Hotel(id, "Grand Hotel", "Paris", "123 Main St",
                4, 150.0, 10, "WiFi, Pool, Spa");
    }

    private Hotel createHotelInCity(Long id, String city) {
        Hotel hotel = createSampleHotel(id);
        hotel.setCity(city);
        return hotel;
    }

    private Hotel createHotelWithPrice(Long id, double price) {
        Hotel hotel = createSampleHotel(id);
        hotel.setPricePerNight(price);
        return hotel;
    }

    private Hotel createHotelWithStarRating(Long id, int rating) {
        Hotel hotel = createSampleHotel(id);
        hotel.setStarRating(rating);
        return hotel;
    }

    private Hotel createHotelWithAvailableRooms(Long id, int availableRooms) {
        Hotel hotel = createSampleHotel(id);
        hotel.setAvailableRooms(availableRooms);
        return hotel;
    }
}
