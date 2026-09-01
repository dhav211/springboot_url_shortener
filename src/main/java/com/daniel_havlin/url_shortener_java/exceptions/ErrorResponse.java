package com.daniel_havlin.url_shortener_java.exceptions;

public record ErrorResponse(String message, String hint, int status) {}