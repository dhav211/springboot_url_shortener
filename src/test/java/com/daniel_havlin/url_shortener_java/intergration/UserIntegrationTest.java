package com.daniel_havlin.url_shortener_java.intergration;

import com.daniel_havlin.url_shortener_java.dto.UrlToShortenRequest;
import com.daniel_havlin.url_shortener_java.models.Url;
import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void loadUrls() {
        urlRepository.save(new Url("abc123", "https://www.google.com"));
        urlRepository.save(new Url("123abc", "https://www.reddit.com"));
    }

    @Test
    void addNewUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest(
                "https://stackoverflow.com/questions/11291933/requestbody-and-responsebody-annotations-in-spring"
        );

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertThat(urlRepository.existsByFullUrl(request.getUrl())).isTrue();
        assertThat(urlRepository.count()).isEqualTo(3);
    }

    @Test
    void failureOnAddingAlreadyTakenUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest("https://www.google.com");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(409));

        assertThat(urlRepository.count()).isEqualTo(2);
    }

    @Test
    void failureOnMaliciousUrl() throws Exception {
        UrlToShortenRequest request = new UrlToShortenRequest("http://malware.testing.google.test/testing/malware/");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422));

        assertThat(urlRepository.count()).isEqualTo(2);
    }

    @Test
    void redirectUserToGoogleByShortCode() throws Exception {
        mockMvc.perform(get("/abc123")).andExpect(status().is(302));
    }

    @Test
    void failToRedirectWithWrongShortCode() throws Exception {
        mockMvc.perform(get("/cba321")).andExpect(status().is(404));
    }
}
