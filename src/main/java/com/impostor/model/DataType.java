package com.impostor.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataType {
    NUMBER("([0-9]+)"), STRING("([0-9a-zA-Z.\\*_\\-]+)");

    private final String regex;

}
