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

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UrlService urlService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void successfullyShortenUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse(
                "abc123",
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );

        when(urlService.isUrlAlreadyShortened(anyString())).thenReturn(false);
        when(urlService.isValidUrl(anyString())).thenReturn(true);
        when(urlService.isFunctioningUrl(anyString())).thenReturn(true);
        when(urlService.isSafeUrl(anyString())).thenReturn(true);
        when(urlService.generateShortCode()).thenReturn("abc123");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"));

        verify(urlService).createShortenedUrl(any(ShortenedUrlResponse.class));
    }

    @Test
    void failToShortenUrlAlreadyInUse() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );
        ShortenedUrlResponse shortenedUrlResponse = new ShortenedUrlResponse(
                "abc123",
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );

        when(urlService.isUrlAlreadyShortened(anyString())).thenReturn(true);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.message").value(request.getUrl() + " is already taken"));
    }

    @Test
    void failToShortenInvalidUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "htps://thisurlwontwork.com/because/it/is-bad/"
        );

        when(urlService.isUrlAlreadyShortened(anyString())).thenReturn(false);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.hint").value("You've given an invalid url, try it in your browser and correct any mistakes."))
                .andExpect(jsonPath("$.message").value("Invalid URL at " + request.getUrl()));
    }

    @Test
    void failToShortenNonfunctioningUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "https://www.thisurlwontwork.com/because/it/doesnt-exisit/"
        );

        when(urlService.isUrlAlreadyShortened(anyString())).thenReturn(false);
        when(urlService.isValidUrl(anyString())).thenReturn(true);
        when(urlService.isFunctioningUrl(anyString())).thenReturn(false);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.hint").value("Address given cannot be reached, are you sure it's a functioning address?"))
                .andExpect(jsonPath("$.message").value(request.getUrl() + " cannot be reached, it is not a functioning URL"));
    }

    @Test
    void failToShortenMaliciousUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "https://www.thisisgonnagiveyouavirus.com/"
        );

        when(urlService.isUrlAlreadyShortened(anyString())).thenReturn(false);
        when(urlService.isValidUrl(anyString())).thenReturn(true);
        when(urlService.isFunctioningUrl(anyString())).thenReturn(true);
        when(urlService.isSafeUrl(anyString())).thenReturn(false);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.hint").value("Woah buddy, we don't want anything dangerous here, put the URL away."))
                .andExpect(jsonPath("$.message").value(request.getUrl() + " is not a safe URL"));
    }

    @Test
    void successfullyRedirectOnValidUrl() throws Exception {
        String shortCode = "abc123";
        String fullUrl = "https://www.thisisalong.com/url/that/we/will/shorten/";

        when(urlService.findUrlByShortCode(shortCode))
                .thenReturn(Optional.of(fullUrl));

        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(fullUrl));
    }

    @Test
    void unsuccessfulRedirectOnInvalidShortCode() throws Exception {
        when(urlService.findUrlByShortCode(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/aabbcc"))
                .andExpect(status().isOk())
                .andExpect(view().name("no-short-code-error"));
    }
}
