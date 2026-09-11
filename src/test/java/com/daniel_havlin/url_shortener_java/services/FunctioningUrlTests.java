package com.daniel_havlin.url_shortener_java.services;

import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpMethod.HEAD;

@ExtendWith(MockitoExtension.class)
public class FunctioningUrlTests {
    private final String browserInfo = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private Random random;

    @Mock
    private HttpClient httpClient;

    private MockRestServiceServer mockServer;
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        urlService = new UrlService(urlRepository, httpClient, builder.build(), new ShortCodeGenerator(random));
    }

    @Test
    void returnsTrueFor2xxResponse() {
        mockServer.expect(requestTo("https://www.google.com/"))
                .andExpect(method(HEAD))
                .andExpect(header("User-Agent", browserInfo))
                .andRespond(withStatus(HttpStatus.OK));

        boolean result = urlService.isFunctioningUrl("https://www.google.com/");

        assertThat(result).isTrue();
        mockServer.verify();
    }

    void returnsTrueFor403RobotBlockedResponse() {
        mockServer.expect(requestTo("https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"))
                .andExpect(method(HEAD))
                .andExpect(header("User-Agent", browserInfo))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        boolean result = urlService.isFunctioningUrl("https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring");

        assertThat(result).isTrue();
        mockServer.verify();
    }

    @Test
    void returnsFalseFor422NonFunctioningUrl() {
        mockServer.expect(requestTo("https://www.soilpasnjkweklrj234lkjs.com"))
                .andExpect(method(HEAD))
                .andExpect(header("User-Agent", browserInfo))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_CONTENT));

        boolean result = urlService.isFunctioningUrl("https://www.soilpasnjkweklrj234lkjs.com");
        assertThat(result).isFalse();
        mockServer.verify();
    }

    @Test
    void returnsFalseFor422InvalidCharactersInUri() {
        String url = "https://www.big deals.com/we/can/shorten/and/save you/money";
        mockServer.expect(requestTo(url))
                .andExpect(method(HEAD))
                .andExpect(header("User-Agent", browserInfo))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_CONTENT));

        boolean result = urlService.isFunctioningUrl(url);
        assertThat(result).isFalse();
    }
}
