# Unit Tests

This folder contains comprehensive unit tests for the Trip Platform service layer.

## Test Files

### 1. BookingServiceImplTest.java
- **24 unit tests** covering `BookingServiceImpl`
- Tests CRUD operations (Create, Read, Update, Delete)
- Tests validation logic (null checks, invalid data)
- Tests business logic (confirm/cancel bookings)
- Tests search functionality (by user, status, service type)

### 2. TourServiceImplTest.java
- **21 unit tests** covering `TourServiceImpl`
- Tests CRUD operations
- Tests input validation (names, prices, duration)
- Tests search methods (by destination, duration, price range)

### 3. HotelServiceImplTest.java
- **25 unit tests** covering `HotelServiceImpl`
- Tests CRUD operations
- Tests star rating validation (0-5 range)
- Tests search functionality (by city, price, rating)
- Tests room booking logic

## Test Results

```
Tests run: 70
Failures: 0
Errors: 0  
Skipped: 0
Success Rate: 100%
```

## Test Framework
- **JUnit 5** - Modern testing framework
- **Mockito** - Mocking framework for dependencies
- **MockitoExtension** - JUnit 5 integration

## Test Coverage
These tests cover:
- ✅ All CRUD operations
- ✅ Validation logic and edge cases
- ✅ Business logic methods
- ✅ Search and filter functionality
- ✅ Error handling with proper exceptions
