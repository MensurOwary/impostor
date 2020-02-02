package com.impostor;

import com.impostor.model.DataType;
import com.impostor.model.Endpoints;
import com.impostor.model.config.Endpoint;
import com.impostor.model.config.EndpointsConfig;
import com.impostor.model.config.UrlPath;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigProcessor {
    private final static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("(\\{\\w+:?\\w+?})");
    private static Endpoints ENDPOINTS_CONFIG;

    static void initialize(String configUrl) {
        if (configUrl == null) throw new IllegalArgumentException("Configuration path is null");
        ENDPOINTS_CONFIG = setEndpoints(configUrl);
    }

    public static Endpoints getEndpoints() {
        return ENDPOINTS_CONFIG;
    }

    private static Endpoints setEndpoints(String configUrl) {
        final EndpointsConfig endpointsConfig = loadConfiguration(configUrl);
        final List<Endpoint> endpoints = endpointsConfig.getEndpoints()
                .stream()
                .map(endpoint -> {
                    final String path = endpoint.getPath();
                    final String contentType = endpoint.getContentType();
                    final String payload = endpoint.getPayload();
                    final String method = endpoint.getMethod();
                    return new Endpoint(pathResolver(path), method, contentType, payload);
                }).collect(Collectors.toList());
        return new Endpoints(endpoints);
    }

    @SneakyThrows
    private static EndpointsConfig loadConfiguration(String configUrl) {
        final Yaml yaml = new Yaml(new Constructor(EndpointsConfig.class));
        return yaml.load(new FileInputStream(configUrl));
    }

    private static UrlPath pathResolver(String originalUrl) {
        String regexUrl = originalUrl;
        final List<String> matchedSegments = PATH_VARIABLE_PATTERN.matcher(regexUrl)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        final Map<String, DataType> pathVariables = new LinkedHashMap<>();
        for (String group : matchedSegments) {
            final var parts = group.substring(1, group.length()-1).split(":");
            final var pathVariable = parts[0];
            final var dataType = DataType.valueOf(parts[1].trim().toUpperCase());
            regexUrl = regexUrl.replace(group, dataType.getRegex());
            pathVariables.put(pathVariable, dataType);
        }
        return new UrlPath(originalUrl, regexUrl, pathVariables);
    }

}
