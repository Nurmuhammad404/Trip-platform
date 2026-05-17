package com.epam.trip.startup.impl;

import com.epam.trip.startup.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig implements Config {
    private final Properties properties;

    public PropertiesConfig() {
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("app.properties")) {
            if (input == null) {
                System.err.println("Unable to find app.properties");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get boolean property value.
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Get integer property value.
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}