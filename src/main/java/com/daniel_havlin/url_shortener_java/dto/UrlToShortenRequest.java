package com.daniel_havlin.url_shortener_java.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlToShortenRequest(@NotBlank @URL String url) { }
