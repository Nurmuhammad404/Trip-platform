package com.epam.trip.startup;

import com.epam.trip.controller.Controller;
import com.epam.trip.controller.ControllerFactory;
import com.epam.trip.startup.impl.PropertiesConfig;
import com.epam.trip.view.View;
import com.epam.trip.view.ViewFactory;
import com.epam.trip.view.impl.ConsoleView;

public class Application {
    private final View view;
    private final Controller controller;
    private final Config config;
    private boolean running;

    public Application() {
        // Load configuration
        this.config = new PropertiesConfig();

        // Initialize View layer
        this.view = ViewFactory.getView();

        // Initialize Controller layer (which injects Services and DAOs)
        this.controller = ControllerFactory.getController(view);

        this.running = false;
    }

    public void start() {
        running = true;

        // Display welcome screen
        if (view instanceof ConsoleView) {
            ConsoleView consoleView = (ConsoleView) view;
            consoleView.displayWelcome();
            consoleView.displayDeveloperInfo();
        }

        // Main application loop
        while (running) {
            try {
                if (view instanceof ConsoleView) {
                    ((ConsoleView) view).displayMainMenu();
                }

                String userInput = view.getUserInput();
                controller.processRequest(userInput);

            } catch (Exception e) {
                view.displayMessage("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        view.displayMessage("Application stopped.");
    }

    public Config getConfig() {
        return config;
    }
}
