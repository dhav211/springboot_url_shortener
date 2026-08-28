package com.daniel_havlin.url_shortener_java.services;

import com.daniel_havlin.url_shortener_java.models.Url;
import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private Random random;

    @InjectMocks
    private UrlService urlService;

    @Test
    @DisplayName("Creates a short code that hasn't been created before in database")
    void createUniqueShortCodeOnFirstAttempt() {
        when(random.nextInt(0, 16)).thenReturn(0, 1, 2, 6, 7, 8);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);

        String shortCode = urlService.generateShortCode();

        assertEquals("abc123", shortCode);
        assertTrue(shortCode.matches("^[abcdef1234567890]{6}$"), "code should only use the allowed alphabet");
        verify(urlRepository, times(1)).existsByShortCode("abc123");
    }

    @Test
    @DisplayName("Creates a short code on the second attempt, first attempt already was used.")
    void createUniqueShortCodeOnSecondAttempt() {
        when(random.nextInt(0, 16))
                .thenReturn(
                        0, 1, 2, 6, 7, 8,
                        6, 7, 8, 0, 1, 2
                );

        when(urlRepository.existsByShortCode("abc123")).thenReturn(true);
        when(urlRepository.existsByShortCode("123abc")).thenReturn(false);

        String shortCode = urlService.generateShortCode();

        assertEquals("123abc", shortCode);
        verify(urlRepository).existsByShortCode("abc123");
        verify(urlRepository).existsByShortCode("123abc");
    }

    @Test
    void findUrlByShortCode() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(new Url("abc123", "https://www.google.com/")));

        Optional<String> fullUrl = urlService.findUrlByShortCode("abc123");
        if (fullUrl.isPresent()) {
        assertEquals("https://www.google.com/", fullUrl.get());
        } else {
            Assertions.fail("Url couldn't be found by short code abc123");
        }
    }

    @Test
    void urlNotFoundByShortCode() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.empty());

        Optional<String> fullUrl = urlService.findUrlByShortCode("abc123");
        assertTrue(fullUrl.isEmpty());
    }

    @Test
    void testValidUrlSyntax() {
        assertTrue(urlService.isValidUrl("https://www.google.com/"));
    }

    @Test
    void testInvalidUrlSyntax() {
        assertFalse(urlService.isValidUrl("hppts::/www.google.coom"));
    }

    @Test
    void testFunctioningUrl() {
        assertTrue(urlService.isFunctioningUrl("https://www.google.com/"));
    }

    @Test
    void testBlockedFunctioningUrl() {
        assertTrue(urlService.isFunctioningUrl("https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"));
    }

    @Test
    void testNonfunctioningUrl() {
        assertFalse(urlService.isFunctioningUrl("https://www.soilpasnjkweklrj234lkjs.com"));
    }
}
