package com.impostor.model.config;

import com.impostor.exception.EndpointNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointsConfig {

    private List<EndpointConfig> endpoints;

    public EndpointConfig getEndpoint(String path) {
        return endpoints.stream()
                .filter(endpoint -> endpoint.getPath().equalsIgnoreCase(path))
                .findFirst()
                .orElseThrow(() -> new EndpointNotFoundException("Endpoint not found"));
    }

}
