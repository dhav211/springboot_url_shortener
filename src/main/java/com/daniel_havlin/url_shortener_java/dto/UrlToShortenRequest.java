package com.daniel_havlin.url_shortener_java.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class UrlToShortenRequest {
    @NotBlank(message = "Url must not be blank")
    @URL(regexp = "^https?://.*", message = "Must be a valid http/https Url")
    String url;

    public UrlToShortenRequest() {}
    public UrlToShortenRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

