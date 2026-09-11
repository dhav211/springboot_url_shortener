package com.daniel_havlin.url_shortener_java.services;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.exceptions.FailedToCreateUrlException;
import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integration")
@SpringBootTest
public class UrlSaveIntegrationTests {
    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void successfullySaveUrlToRepository() {
        ShortenedUrlResponse urlResponse = new ShortenedUrlResponse("abc123", "https://www.google.com");
        urlService.createShortenedUrl(urlResponse);

        assertThat(urlRepository.count()).isEqualTo(1);
        assertThat(urlRepository.existsByShortCode(urlResponse.shortCode())).isTrue();
    }

    @Test
    void rejectUrlResponseWithInvalidParameters() {
        assertThrows(FailedToCreateUrlException.class, () -> urlService.createShortenedUrl(new ShortenedUrlResponse("", "https://www.google.com")));
        assertThrows(FailedToCreateUrlException.class, () -> urlService.createShortenedUrl(new ShortenedUrlResponse("abc123", "")));
        assertThrows(FailedToCreateUrlException.class, () -> urlService.createShortenedUrl(new ShortenedUrlResponse(null, null)));
    }
}
