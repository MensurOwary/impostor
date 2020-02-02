package com.impostor.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointsConfig {

    private List<EndpointConfig> endpoints;

}
