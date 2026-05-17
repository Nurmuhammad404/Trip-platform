package com.epam.trip.service.impl;

import com.epam.trip.dao.GenericDao;
import com.epam.trip.entity.Tour;
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
 * Unit tests for TourServiceImpl
 * Tests all CRUD operations and search functionality
 */
@ExtendWith(MockitoExtension.class)
class TourServiceImplTest {

    @Mock
    private GenericDao<Tour> mockTourDao;

    private TourServiceImpl tourService;

    @BeforeEach
    void setUp() {
        tourService = new TourServiceImpl(mockTourDao);
    }

    @Test
    void testFindById_ValidId_ReturnsTour() {
        // Arrange
        Long tourId = 1L;
        Tour expectedTour = createSampleTour(tourId);
        when(mockTourDao.findById(tourId)).thenReturn(expectedTour);

        // Act
        Tour actualTour = tourService.findById(tourId);

        // Assert
        assertNotNull(actualTour);
        assertEquals(expectedTour.getId(), actualTour.getId());
        assertEquals(expectedTour.getName(), actualTour.getName());
        verify(mockTourDao, times(1)).findById(tourId);
    }

    @Test
    void testFindById_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.findById(null));
        assertEquals("Invalid tour ID", exception.getMessage());
        verify(mockTourDao, never()).findById(any());
    }

    @Test
    void testFindById_ZeroId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.findById(0L));
        assertEquals("Invalid tour ID", exception.getMessage());
    }

    @Test
    void testFindAll_ReturnsAllTours() {
        // Arrange
        List<Tour> expectedTours = Arrays.asList(
                createSampleTour(1L),
                createSampleTour(2L),
                createSampleTour(3L));
        when(mockTourDao.findAll()).thenReturn(expectedTours);

        // Act
        List<Tour> actualTours = tourService.findAll();

        // Assert
        assertNotNull(actualTours);
        assertEquals(3, actualTours.size());
        verify(mockTourDao, times(1)).findAll();
    }

    @Test
    void testSave_ValidTour_SavesTour() {
        // Arrange
        Tour tour = createSampleTour(null);

        // Act
        tourService.save(tour);

        // Assert
        verify(mockTourDao, times(1)).save(tour);
    }

    @Test
    void testSave_NullTour_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(null));
        assertEquals("Tour cannot be null", exception.getMessage());
        verify(mockTourDao, never()).save(any());
    }

    @Test
    void testSave_NullName_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);
        tour.setName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(tour));
        assertEquals("Tour name is required", exception.getMessage());
    }

    @Test
    void testSave_EmptyName_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);
        tour.setName("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(tour));
        assertEquals("Tour name is required", exception.getMessage());
    }

    @Test
    void testSave_NullDestination_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);
        tour.setDestination(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(tour));
        assertEquals("Destination is required", exception.getMessage());
    }

    @Test
    void testSave_ZeroDuration_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);
        tour.setDuration(0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(tour));
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test
    void testSave_NegativeDuration_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);
        tour.setDuration(-5);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(tour));
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test
    void testSave_NegativePrice_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);
        tour.setPrice(-100.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.save(tour));
        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    void testUpdate_ValidTour_UpdatesTour() {
        // Arrange
        Tour tour = createSampleTour(1L);

        // Act
        tourService.update(tour);

        // Assert
        verify(mockTourDao, times(1)).update(tour);
    }

    @Test
    void testUpdate_NullId_ThrowsException() {
        // Arrange
        Tour tour = createSampleTour(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.update(tour));
        assertEquals("Tour ID cannot be null for update", exception.getMessage());
        verify(mockTourDao, never()).update(any());
    }

    @Test
    void testDelete_ValidId_DeletesTour() {
        // Arrange
        Long tourId = 1L;

        // Act
        tourService.delete(tourId);

        // Assert
        verify(mockTourDao, times(1)).delete(tourId);
    }

    @Test
    void testDelete_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tourService.delete(null));
        assertEquals("Invalid tour ID", exception.getMessage());
    }

    @Test
    void testSearchByDestination_FindsMatchingTours() {
        // Arrange
        List<Tour> allTours = Arrays.asList(
                createTourWithDestination(1L, "Paris, France"),
                createTourWithDestination(2L, "Rome, Italy"),
                createTourWithDestination(3L, "Paris Night Tour"));
        when(mockTourDao.findAll()).thenReturn(allTours);

        // Act
        List<Tour> result = tourService.searchByDestination("paris");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getDestination().toLowerCase().contains("paris")));
    }

    @Test
    void testSearchByDestination_CaseInsensitive() {
        // Arrange
        List<Tour> allTours = Arrays.asList(
                createTourWithDestination(1L, "PARIS, FRANCE"),
                createTourWithDestination(2L, "Rome, Italy"));
        when(mockTourDao.findAll()).thenReturn(allTours);

        // Act
        List<Tour> result = tourService.searchByDestination("paris");

        // Assert
        assertEquals(1, result.size());
        assertEquals("PARIS, FRANCE", result.get(0).getDestination());
    }

    @Test
    void testSearchByDuration_FindsToursWithinDuration() {
        // Arrange
        List<Tour> allTours = Arrays.asList(
                createTourWithDuration(1L, 3),
                createTourWithDuration(2L, 7),
                createTourWithDuration(3L, 5));
        when(mockTourDao.findAll()).thenReturn(allTours);

        // Act
        List<Tour> result = tourService.searchByDuration(5);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getDuration() <= 5));
    }

    @Test
    void testSearchByPriceRange_FindsToursInRange() {
        // Arrange
        List<Tour> allTours = Arrays.asList(
                createTourWithPrice(1L, 500.0),
                createTourWithPrice(2L, 1500.0),
                createTourWithPrice(3L, 800.0),
                createTourWithPrice(4L, 2500.0));
        when(mockTourDao.findAll()).thenReturn(allTours);

        // Act
        List<Tour> result = tourService.searchByPriceRange(500.0, 1500.0);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(t -> t.getPrice() >= 500.0 && t.getPrice() <= 1500.0));
    }

    @Test
    void testSearchByPriceRange_ExactBoundaries() {
        // Arrange
        List<Tour> allTours = Arrays.asList(
                createTourWithPrice(1L, 1000.0),
                createTourWithPrice(2L, 2000.0));
        when(mockTourDao.findAll()).thenReturn(allTours);

        // Act
        List<Tour> result = tourService.searchByPriceRange(1000.0, 2000.0);

        // Assert
        assertEquals(2, result.size());
    }

    // Helper methods for creating test data

    private Tour createSampleTour(Long id) {
        return new Tour(id, "European Adventure", "Paris, France", 7,
                1200.0, "Amazing tour of Paris", 15, "John Smith", "Daily at 9 AM");
    }

    private Tour createTourWithDestination(Long id, String destination) {
        Tour tour = createSampleTour(id);
        tour.setDestination(destination);
        return tour;
    }

    private Tour createTourWithDuration(Long id, int duration) {
        Tour tour = createSampleTour(id);
        tour.setDuration(duration);
        return tour;
    }

    private Tour createTourWithPrice(Long id, double price) {
        Tour tour = createSampleTour(id);
        tour.setPrice(price);
        return tour;
    }
}
