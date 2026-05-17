# Trip Platform

A comprehensive travel management system built with Java, implementing proper layered architecture for booking flights, hotels, cars, taxis, and tours.

## 🎯 Project Overview

Trip Platform is a complete travel management solution that allows users to:
- ✈️ Book flights
- 🏨 Reserve hotels
- 🚗 Rent cars
- 🗺️ Discover tourist attractions
- 🎒 Join group tours
- 🚕 Book taxi services
- 📋 Manage bookings

## 🏗️ Architecture

This project follows a strict **layered architecture** pattern:

```
┌─────────────────────────────────────┐
│         View Layer (Console UI)     │
├─────────────────────────────────────┤
│         Controller Layer            │
├─────────────────────────────────────┤
│         Service Layer               │
│     (Business Logic & Validation)   │
├─────────────────────────────────────┤
│         DAO Layer                   │
│     (Data Access & Persistence)     │
├─────────────────────────────────────┤
│         Entity Layer                │
│     (Domain Models)                 │
└─────────────────────────────────────┘
```

### Layer Details

- **Entity Layer**: 9 entities (User, Flight, Hotel, Car, Place, Tour, Taxi, Booking)
- **DAO Layer**: CSV-backed persistence with in-memory cache
- **Service Layer**: Business logic, validation, and search functionality
- **Controller Layer**: Command routing and request handling
- **View Layer**: Console-based user interface
- **Startup Layer**: Manual dependency injection (no Spring)

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/Nurmuhammad404/Trip-platform.git
cd trip-platform
```

2. Compile the project:
```bash
mvn clean compile
```

3. Run the application:
```bash
mvn exec:java -Dexec.mainClass="com.epam.trip.Main"
```

## 📁 Project Structure

```
trip-platform/
├── src/
│   ├── main/
│   │   ├── java/com/epam/trip/
│   │   │   ├── entity/          # Domain models
│   │   │   ├── dao/             # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   ├── controller/      # Request handling
│   │   │   ├── view/            # User interface
│   │   │   ├── startup/         # Application bootstrap
│   │   │   ├── exception/       # Custom exceptions
│   │   │   └── Main.java        # Entry point
│   │   └── resources/
│   │       ├── app.properties   # Configuration
│   │       └── data/            # CSV data files
│   └── test/                    # Unit tests
├── pom.xml                      # Maven configuration
└── README.md
```

## ✨ Features

### Core Functionality
- **User Management**: Registration and authentication
- **Flight Booking**: Search flights by route, book seats
- **Hotel Reservation**: Search by city, price range, star rating
- **Car Rental**: Search by location, type, availability
- **Tourist Places**: Browse attractions with ratings
- **Group Tours**: Discover and join guided tours
- **Taxi Service**: Book taxis by city with driver ratings
- **Booking Management**: View and manage all bookings

### Technical Features
- ✅ Layered architecture with loose coupling
- ✅ Factory pattern for dependency injection
- ✅ CSV-based data persistence
- ✅ In-memory caching for performance
- ✅ Comprehensive validation
- ✅ Search and filtering capabilities

## 📊 Sample Data

The project includes sample data for:
- 3 users
- 5 flights (AA, UA, DL, SW, BA)
- 4 hotels (New York, Miami, Chicago, Denver)
- 5 rental cars
- 6 tourist places
- 4 group tours
- 5 taxi drivers
- 3 sample bookings

## 🛠️ Technologies

- **Java 11**: Core programming language
- **Maven**: Build and dependency management
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for tests
- **CSV**: Data storage

## 📝 Design Patterns

- **Factory Pattern**: For creating DAO and Service instances
- **Singleton Pattern**: For factory instances
- **DAO Pattern**: For data access abstraction
- **MVC Pattern**: For application structure

## 🎓 Course Project

This project was developed as part of the EPAM University Program, demonstrating:
- Object-oriented programming principles
- Layered architecture design
- SOLID principles
- Design patterns
- Testing best practices

## 📄 License

This project is developed for educational purposes as part of a university course.

## 👤 Author

**Nurmuhammad**
- Course: Java Development
- University: EPAM University Program
- Year: 2025

## 🙏 Acknowledgments

- EPAM University Program for the project requirements
- Instructors for guidance on layered architecture