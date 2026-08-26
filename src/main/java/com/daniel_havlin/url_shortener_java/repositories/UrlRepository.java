package com.daniel_havlin.url_shortener_java.repositories;

import com.daniel_havlin.url_shortener_java.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    Optional<String> findByFullUrl(String fullUrl);
    Boolean existsByShortCode(String shortCode);
}
