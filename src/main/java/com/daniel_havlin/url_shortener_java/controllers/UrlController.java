package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.dto.UrlToShortenRequest;
import com.daniel_havlin.url_shortener_java.exceptions.InvalidUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.NonfunctioningUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.NotSafeUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.UrlTakenException;
import com.daniel_havlin.url_shortener_java.services.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Controller
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenedUrlResponse> shortenUrl(@RequestBody UrlToShortenRequest urlToShortenRequest) {
        if (urlService.isUrlAlreadyShortened(urlToShortenRequest.getUrl())) {
            throw new UrlTakenException(
                    urlToShortenRequest.getUrl() + " is already taken",
                    urlService.findShortCodeByUrl(urlToShortenRequest.getUrl())
            );
        }

        if (!urlService.isValidUrl(urlToShortenRequest.getUrl())) {
            throw new InvalidUrlException("Invalid URL at " + urlToShortenRequest.getUrl());
        }

        if (!urlService.isFunctioningUrl(urlToShortenRequest.getUrl())) {
            throw new NonfunctioningUrlException(urlToShortenRequest.getUrl() + " cannot be reached, it is not a functioning URL");
        }

        if (!urlService.isSafeUrl(urlToShortenRequest.getUrl())) {
            throw new NotSafeUrlException(urlToShortenRequest.getUrl() + " is not a safe URL");
        }

        String shortCode = urlService.generateShortCode();
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse(shortCode, urlToShortenRequest.getUrl());
        urlService.createShortenedUrl(shortenedUrlResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenedUrlResponse);
    }

    @GetMapping("/{shortCode:(?!favicon\\.ico).*}")
    public String redirectToShortCode(@PathVariable String shortCode) {
        Optional<String> fullUrl = urlService.findUrlByShortCode(shortCode);

        return fullUrl
                .map(url -> "redirect:" + url)
                .orElse("no-short-code-error");
    }
}
