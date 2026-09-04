package com.daniel_havlin.url_shortener_java.services;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@SpringBootTest
public class FunctioningUrlIntegrationTests {
    @Autowired
    private UrlService urlService;

    @Test
    void testFunctioningUrl() {
        assertTrue(urlService.isFunctioningUrl("https://www.google.com/"));
    }

    @Test
    void testBlockedFunctioningUrl() {
        assertTrue(urlService.isFunctioningUrl("https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"));
    }

    @Test
    void testUrlWithPercentages() {
        assertTrue(urlService.isFunctioningUrl("https://en.wikipedia.org/wiki/Ang%C3%A9lique_Kidjo"));
    }

    @Test
    void testNonfunctioningUrl() {
        assertFalse(urlService.isFunctioningUrl("https://www.soilpasnjkweklrj234lkjs.com"));
    }
}
