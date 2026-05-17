package com.epam.trip.view.impl;

import com.epam.trip.view.View;
import java.util.Scanner;

public class ConsoleView implements View {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String getUserInput() {
        return scanner.nextLine().trim();
    }

    public void displayWelcome() {
        displayMessage("\n" +
                "╔═══════════════════════════════════════════════════════════╗\n" +
                "║                                                           ║\n" +
                "║              ✈️  TRIP PLATFORM  ✈️                       ║\n" +
                "║                                                           ║\n" +
                "║        Your Complete Travel Management Solution           ║\n" +
                "║                                                           ║\n" +
                "╚═══════════════════════════════════════════════════════════╝");
    }

    public void displayMainMenu() {
        displayMessage("\n==================== MAIN MENU ====================");
        displayMessage("  1. ✈️  Flights        - Book domestic & international flights");
        displayMessage("  2. 🏨 Hotels         - Reserve hotel rooms");
        displayMessage("  3. 🚗 Car Rental     - Rent vehicles");
        displayMessage("  4. 🗺️  Places         - Discover tourist attractions");
        displayMessage("  5. 🎒 Tours          - Join group tours");
        displayMessage("  6. 🚕 Taxis          - Book taxi services");
        displayMessage("  7. 📋 My Bookings    - View your bookings");
        displayMessage("  8. 👤 Login          - Sign in");
        displayMessage("  9. 🚪 Exit           - Close application");
        displayMessage("===================================================");
        displayMessage("Enter your choice (1-9): ");
    }

    public void displayDeveloperInfo() {
        displayMessage("\nDeveloper: Nurmuhammad");
        displayMessage("Version: 1.0.0");
        displayMessage("Course Project - EPAM University Program");
    }

    public void displayError(String errorMessage) {
        displayMessage("\n❌ ERROR: " + errorMessage);
    }

    public void displaySuccess(String successMessage) {
        displayMessage("\n✅ SUCCESS: " + successMessage);
    }

    public void displaySeparator() {
        displayMessage("\n---------------------------------------------------");
    }
}