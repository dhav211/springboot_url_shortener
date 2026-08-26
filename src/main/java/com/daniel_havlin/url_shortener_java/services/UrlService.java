package com.daniel_havlin.url_shortener_java.services;

import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final Random random;

    public UrlService(UrlRepository urlRepository, Random random) {
        this.urlRepository = urlRepository;
        this.random = random;
    }

    public String createShortCode() {
        StringBuilder sb = new StringBuilder();
        boolean hasFoundNewCode = false;
        String[] shortCodeLetters = {"a", "b", "c", "d", "e", "f", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        do {
            sb.delete(0, sb.length());
            for (int i = 0; i < 6; i++) {
                sb.append(shortCodeLetters[random.nextInt(0, shortCodeLetters.length)]);
            }

            hasFoundNewCode = !urlRepository.existsByShortCode(sb.toString());
        } while (!hasFoundNewCode);

        return sb.toString();
    }
}
