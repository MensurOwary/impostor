package com.impostor.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS;

    public static HttpMethod getMethodOrDefault(String method, HttpMethod defaultMethod) {
        try {
            return HttpMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException iax) {
            throw new IllegalArgumentException("No such HttpMethod exists");
        } catch (Exception ex) {
            return defaultMethod;
        }
    }
}
