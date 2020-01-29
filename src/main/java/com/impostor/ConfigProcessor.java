package com.impostor;

import com.impostor.model.DataType;
import com.impostor.model.Endpoints;
import com.impostor.model.config.EndpointsConfig;
import com.impostor.model.config.UrlPath;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigProcessor {
    private final static Pattern compile = Pattern.compile("(\\{\\w+:?\\w+?})");
    public final static EndpointsConfig ENDPOINTS_CONFIG = process();

    private static EndpointsConfig process() {
        final Yaml yaml = new Yaml(new Constructor(EndpointsConfig.class));
        final InputStream inputStream = ConfigProcessor.class
                .getClassLoader()
                .getResourceAsStream("config.yml");
        return yaml.load(inputStream);
    }

    private static void pathResolver(String path) {
        String original = new String(path);
        final var matcher = compile.matcher(path);
        final List<String> collect = matcher.results()
                .map(MatchResult::group).collect(Collectors.toList());
        for (String group : collect) {
            final var parts = group.substring(1, group.length()-1).split(":");
            final var pathVariable = parts[0];
            final var dataType = DataType.valueOf(parts[1].trim().toUpperCase());
            path = path.replace(group, dataTypeToRegexResolver(dataType));
        }
        System.out.println(path);
        System.out.println(original);
        System.out.println("/path/123/hello/dasdas".matches(path));
    }

    private static String dataTypeToRegexResolver(DataType dataType) {
        switch (dataType) {
            case NUMBER:
                return "[0-9]+";
            case STRING:
                return "[0-9a-zA-Z.\\*_\\-]+";
            default:
                throw new RuntimeException("Illegal data type");
        }
    }

    public static void main(String[] args) {
        pathResolver("/path/{id:number}/hello/{pre:string}");
    }

}
