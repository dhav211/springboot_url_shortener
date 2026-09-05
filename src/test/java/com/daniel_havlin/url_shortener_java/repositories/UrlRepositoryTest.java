package com.daniel_havlin.url_shortener_java.repositories;

import com.daniel_havlin.url_shortener_java.models.Url;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UrlRepositoryTest {
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void addDefaultUrls() {
        Url url1 = new Url("abc123", "https://www.swiftbysundell.com/basics/map-flatmap-and-compactmap/");
        Url url2 = new Url("123abc", "https://github.com/dhav211/springboot_url_shortener");
        entityManager.persistAndFlush(url1);
        entityManager.persistAndFlush(url2);
    }


    @Test
    void addNewUrl() {
        Url url = new Url("a1b2c3", "https://www.baeldung.com/java-init-list-one-line");
        entityManager.persistAndFlush(url);

        List<Url> urls = urlRepository.findAll();

        assertThat(urls).hasSize(3);
        assertThat(urls).extracting("shortCode").contains("a1b2c3");
    }

    @Test
    void blankValuesThrowException() {
        Url urlWithBlankShortCode = new Url(" ", "https://www.baeldung.com/java-init-list-one-line");
        Url urlWithBlankFullUrl = new Url("aabbcc", "   ");
        Url urlWithAllBlankValues = new Url("  ", "");

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(urlWithBlankShortCode);
            entityManager.persist(urlWithBlankFullUrl);
            entityManager.persist(urlWithAllBlankValues);
        });
    }

    @Test
    void nullValuesThrowException() {
        Url urlWithNullShortCode = new Url(null, "https://www.baeldung.com/java-init-list-one-line");
        Url urlWithNullFullUrl = new Url("aabbcc", null);
        Url urlWithAllNullValues = new Url(null, null);

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(urlWithNullShortCode);
            entityManager.persist(urlWithNullFullUrl);
            entityManager.persist(urlWithAllNullValues);
        });
    }

    @Test
    void nonUniqueValuesThrowException() {
        Url urlWithDuplicateShortCode = new Url("abc123", "https://apnews.com/article/israel-palestinians-west-bank-ambassador-settlers-e33f8c97f368e7babd37c6d8c0a5759d");
        Url urlWithDuplicateFullUrl = new Url("a3cc11", "https://www.swiftbysundell.com/basics/map-flatmap-and-compactmap/");

        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
            entityManager.persist(urlWithDuplicateShortCode);
            entityManager.persist(urlWithDuplicateFullUrl);
        });
    }
}
