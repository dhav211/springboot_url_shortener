package com.daniel_havlin.url_shortener_java.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.Random;

@Component
public class AppConfig {
    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
