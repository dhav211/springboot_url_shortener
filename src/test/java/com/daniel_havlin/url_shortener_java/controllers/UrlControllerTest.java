package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.dto.UrlToShortenRequest;
import com.daniel_havlin.url_shortener_java.services.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UrlService urlService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testShortenUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse(
                "abc123",
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );

        when(urlService.isValidUrl(anyString())).thenReturn(true);
        when(urlService.isFunctioningUrl(anyString())).thenReturn(true);
        when(urlService.isSafeUrl(anyString())).thenReturn(true);
        when(urlService.generateShortCode()).thenReturn("abc123");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"));
    }

}
