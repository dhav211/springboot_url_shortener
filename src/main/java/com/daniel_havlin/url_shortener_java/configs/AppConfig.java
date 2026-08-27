package com.daniel_havlin.url_shortener_java.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AppConfig {
    @Bean
    public Random random() {
        return new Random();
    }
}
