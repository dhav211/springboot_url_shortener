package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.dto.UrlToShortenRequest;
import com.daniel_havlin.url_shortener_java.services.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenedUrlResponse> shortenUrl(@RequestBody UrlToShortenRequest urlToShortenRequest) {
        String shortCode = urlService.createShortCode();
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse(shortCode, "https://www.address.com");
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenedUrlResponse);
    }
}
