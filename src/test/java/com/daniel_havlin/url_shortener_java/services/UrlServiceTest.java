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

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
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

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlService urlService;


    @Test
    @DisplayName("Creates a short code that hasn't been created before in database")
    void createUniqueShortCodeOnFirstAttempt() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(shortCodeGenerator.generate()).thenReturn("abc123");

        String shortCode = urlService.generateShortCode();

        assertEquals("abc123", shortCode);
        assertTrue(shortCode.matches("^[abcdef1234567890]{6}$"), "code should only use the allowed alphabet");
        verify(urlRepository, times(1)).existsByShortCode("abc123");
    }

    @Test
    @DisplayName("Creates a short code on the second attempt, first attempt already was used.")
    void createUniqueShortCodeOnSecondAttempt() {
        when(shortCodeGenerator.generate()).thenReturn("abc123", "123abc");
        when(urlRepository.existsByShortCode("abc123")).thenReturn(true, false);

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
    void shortCodeFoundByFullUrl() {
        when(urlRepository.findByFullUrl("http://www.theurlinthedatabase.com"))
                .thenReturn(Optional.of(new Url("abc123", "http://www.theurlinthedatabase.com")));
        String shortCode = urlService.findShortCodeByUrl("http://www.theurlinthedatabase.com");
        assertEquals("abc123", shortCode);
    }

    @Test
    void returnEmptyStringWhenFullUrlIsNotFound() {
        when(urlRepository.findByFullUrl("http://www.theurlinthedatabase.com")).thenReturn(Optional.empty());

        String emptyShortCode = urlService.findShortCodeByUrl("http://www.theurlinthedatabase.com");
        assertTrue(emptyShortCode.isEmpty());
    }

    @Test
    void returnEmptyStringWhenFullUrlIsEmpty() {
        when(urlRepository.findByFullUrl("")).thenReturn(Optional.empty());

        String emptyShortCode = urlService.findShortCodeByUrl("");
        assertTrue(emptyShortCode.isEmpty());
    }

    @Test
    void testValidUrlSyntax() {
        assertTrue(urlService.isValidUrl("https://www.google.com/"));
    }

    @Test
    void testInvalidUrlsSyntax() {
        assertFalse(urlService.isValidUrl("hppts::/www.google.coom"));
        assertFalse(urlService.isValidUrl("asdkjfa"));
        assertFalse(urlService.isValidUrl(""));
        assertFalse(urlService.isValidUrl("     "));
    }

    private void setFakeApiKey() {
        try {
            var field = urlService.getClass().getDeclaredField("googleSafeBrowsingApiKey");
            field.setAccessible(true);
            field.set(urlService, "fake-key");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSafeUrlWhenReturnsEmptyResponse() throws Exception {
        setFakeApiKey();
        when(httpResponse.body()).thenReturn("{}"); // no "matches" key
        when(httpClient.<String>send(any(), any())).thenReturn(httpResponse);

        boolean result = urlService.isSafeUrl("https://www.google.com");

        assertTrue(result);
    }

    @Test
    void testMaliciousUrlWhenMatchesPresentInResponse() throws Exception {
        setFakeApiKey();
        when(httpResponse.body()).thenReturn("{\"matches\": [{\"threatType\": \"MALWARE\"}]}");
        when(httpClient.<String>send(any(), any())).thenReturn(httpResponse);

        boolean result = urlService.isSafeUrl("https://malicious-example.com");

        assertFalse(result);
    }

}
