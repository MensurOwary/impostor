package com.impostor.model;

import com.impostor.model.config.Endpoint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@ToString
@Getter
@RequiredArgsConstructor
public class Endpoints {
    private final List<Endpoint> endpoints;

    public Optional<Endpoint> getEndpoint(String exactPath) {
        return this.endpoints
                .stream()
                .filter(endpoint -> endpoint.getPath().getRegexUrl().matcher(exactPath).matches())
                .findFirst();
    }
}
