package com.daniel_havlin.url_shortener_java.services;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ShortCodeGenerator {
    private final Random random;

    public ShortCodeGenerator(Random random) {
        this.random = random;
    }
    public String generate() {
        StringBuilder sb = new StringBuilder();
        String[] shortCodeLetters = {"a", "b", "c", "d", "e", "f", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

        for (int i = 0; i < 6; i++) {
            sb.append(shortCodeLetters[random.nextInt(0, shortCodeLetters.length)]);
        }

        return sb.toString();
    }
}
