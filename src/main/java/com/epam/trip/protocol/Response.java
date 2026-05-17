package com.epam.trip.protocol;

public class Response {
    public enum Status { OK, ERROR, UNAUTHORIZED, FORBIDDEN }

    private Status status;
    private String message;
    private Object data;

    private Response() {}

    public static Response ok(String message) {
        Response r = new Response();
        r.status = Status.OK;
        r.message = message;
        return r;
    }

    public static Response ok(String message, Object data) {
        Response r = ok(message);
        r.data = data;
        return r;
    }

    public static Response error(String message) {
        Response r = new Response();
        r.status = Status.ERROR;
        r.message = message;
        return r;
    }

    public static Response unauthorized(String message) {
        Response r = new Response();
        r.status = Status.UNAUTHORIZED;
        r.message = message;
        return r;
    }

    public static Response forbidden(String message) {
        Response r = new Response();
        r.status = Status.FORBIDDEN;
        r.message = message;
        return r;
    }

    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getData() { return data; }

    public boolean isOk() { return status == Status.OK; }
}
