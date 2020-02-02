package com.impostor.model.config;

import com.impostor.model.DataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.regex.Pattern;

@ToString
@Getter
public class UrlPath {

    private final String originalUrl;
    private final Pattern regexUrl;
    private final Map<String, DataType> pathParams;
    private Map<String, String> queryParams;

    public UrlPath(String originalUrl, String regexUrl, Map<String, DataType> pathParams) {
        this.originalUrl = originalUrl;
        this.regexUrl = Pattern.compile(regexUrl);
        this.pathParams = pathParams;
    }
}
