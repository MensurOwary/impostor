package com.impostor.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointConfig {

    private String path;
    private String method;
    private String contentType;
    private String payload;

}
