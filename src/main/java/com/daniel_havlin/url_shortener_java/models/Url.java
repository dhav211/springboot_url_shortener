package com.daniel_havlin.url_shortener_java.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "url", uniqueConstraints = {
        @UniqueConstraint(columnNames = "shortCode"),
        @UniqueConstraint(columnNames = "fullUrl")
})
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(unique = true, nullable = false)
    private String shortCode;

    @NotBlank
    @NotNull
    @Column(unique = true, nullable = false, length = 2048)
    private String fullUrl;

    protected Url() {}

    public Url(String shortCode, String fullUrl) {
        this.shortCode = shortCode;
        this.fullUrl = fullUrl;
    }

    public Long getId() {
        return id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
}
