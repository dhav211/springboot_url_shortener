package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.dto.UrlToShortenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UrlController {
    @PostMapping("/shorten")
    public ResponseEntity<ShortenedUrlResponse> shortenUrl(UrlToShortenRequest urlToShortenRequest) {
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse("abc123", "https://www.address.com");
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenedUrlResponse);
    }
}
