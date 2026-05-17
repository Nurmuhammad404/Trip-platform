package com.epam.trip.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private Command command;
    private String token;
    private Map<String, String> params;

    public Request() {
        this.params = new HashMap<>();
    }

    public Request(Command command, String token, Map<String, String> params) {
        this.command = command;
        this.token = token;
        this.params = (params != null) ? params : new HashMap<>();
    }

    public static Request of(Command command) {
        return new Request(command, null, null);
    }

    public static Request of(Command command, String token) {
        return new Request(command, token, null);
    }

    public Command getCommand() { return command; }
    public void setCommand(Command command) { this.command = command; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public String getParam(String key, String defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    public void setParams(Map<String, String> params) {
        this.params = (params != null) ? params : new HashMap<>();
    }
}
