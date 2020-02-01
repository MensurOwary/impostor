package com.impostor.model.config;

import com.impostor.model.DataType;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@RequiredArgsConstructor
public class UrlPath {

    private final String literalUrl;
    private final String regexUrl;
    private Map<String, String> queryParams;
    private Map<String, DataType> pathParams;

}
