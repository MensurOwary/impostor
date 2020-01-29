package com.impostor.utils

import java.util.regex.Pattern

val pattern: Pattern = Pattern.compile("^(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS).*")

fun String.isValidRequestLine(): Boolean = pattern.matcher(this).matches()