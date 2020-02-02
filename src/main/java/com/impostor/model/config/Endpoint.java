package com.impostor.model.config;

import com.impostor.http.HttpMethod;
import lombok.Getter;
import lombok.ToString;

import static com.impostor.http.HttpMethod.getMethodOrDefault;

@ToString
@Getter
public class Endpoint {

    private final UrlPath path;
    private final HttpMethod method;
    private final String contentType;
    private final String payload;

    public Endpoint(UrlPath path, String method, String contentType, String payload) {
        this.path = path;
        this.method = getMethodOrDefault(method, HttpMethod.GET);
        this.contentType = contentType;
        this.payload = payload;
    }
}
