package com.daniel_havlin.url_shortener_java.services;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GoogleSafeBrowsingTest {
    @Autowired
    private UrlService urlService;

    @Test
    @Tag("integration")
    void testSafeUrl() {
        assertTrue(urlService.isSafeUrl("https://www.google.com"));
    }

    @Test
    @Tag("integration")
    void testUnsafeUrl() {
        assertFalse(urlService.isSafeUrl("http://malware.testing.google.test/testing/malware/"));
    }
}
