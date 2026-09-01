package com.daniel_havlin.url_shortener_java.exceptions;

public class UrlTakenException extends RuntimeException {
    private final String associatedShortCode;
    public UrlTakenException(String message, String associatedShortCode) {
        super(message);
        this.associatedShortCode = associatedShortCode;
    }

    public String getAssociatedShortCode() {
        return associatedShortCode;
    }
}
