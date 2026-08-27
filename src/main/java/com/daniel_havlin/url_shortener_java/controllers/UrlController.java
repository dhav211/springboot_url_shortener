package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.dto.UrlToShortenRequest;
import com.daniel_havlin.url_shortener_java.exceptions.InvalidUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.NonfunctioningUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.NotSafeUrlException;
import com.daniel_havlin.url_shortener_java.services.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
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
        if (!urlService.isValidUrl(urlToShortenRequest.url())) {
            throw new InvalidUrlException("Invalid URL at " + urlToShortenRequest.url());
        }

        if (!urlService.isFunctioningUrl(urlToShortenRequest.url())) {
            throw new NonfunctioningUrlException(urlToShortenRequest.url() + " cannot be reached, it is not a functioning URL");
        }

        if (!urlService.isSafeUrl(urlToShortenRequest.url())) {
            throw new NotSafeUrlException(urlToShortenRequest.url() + " is not a safe URL");
        }

        String shortCode = urlService.generateShortCode();
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse(shortCode, urlToShortenRequest.url());
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenedUrlResponse);
    }
}
