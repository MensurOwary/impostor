package com.impostor.http;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RequestLine {

    private HttpMethod method;
    private String uri;
    private String path;

    public static RequestLine getRequestLine(String requestLine) {
        final String[] segments = requestLine.split("\\s+");
        if (segments.length != 3) {
            return null;
        }
        final HttpMethod method = HttpMethod.valueOf(segments[0].trim().toUpperCase());
        final String uri = segments[1];
        final String protocol = segments[2];
        return new RequestLine(method, uri, protocol);
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS
    }
}
