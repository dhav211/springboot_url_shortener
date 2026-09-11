package com.daniel_havlin.url_shortener_java.services;

import com.daniel_havlin.url_shortener_java.dto.ShortenedUrlResponse;
import com.daniel_havlin.url_shortener_java.exceptions.FailedToCreateUrlException;
import com.daniel_havlin.url_shortener_java.models.Url;
import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private Logger logger = Logger.getLogger(UrlService.class.getName());

    @Value("${googleSafeBrowsingApi.key}")
    private String googleSafeBrowsingApiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlService(UrlRepository urlRepository,
                      HttpClient httpClient,
                      RestClient restClient,
                      ShortCodeGenerator shortCodeGenerator
    ) {
        this.urlRepository = urlRepository;
        this.restClient = restClient;
        this.httpClient = httpClient;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public String generateShortCode() {
        boolean hasFoundNewCode = false;
        String shortCode = "";
        do {
            shortCode = shortCodeGenerator.generate();
            hasFoundNewCode = !urlRepository.existsByShortCode(shortCode);
        } while (!hasFoundNewCode);

        return shortCode;
    }

    public void createShortenedUrl(ShortenedUrlResponse shortenedUrlResponse) {
        try {
            urlRepository.save(new Url(shortenedUrlResponse.shortCode(), shortenedUrlResponse.fullUrl()));
        } catch (Exception e) {
            throw new FailedToCreateUrlException("Failure to save Url entity to database: " + e.getMessage());
        }
    }

    public Optional<String> findUrlByShortCode(String shortCode) {
        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        return url.map(Url::getFullUrl);
    }

    public String findShortCodeByUrl(String url) {
        Optional<Url> databaseUrl = urlRepository.findByFullUrl(url);
        return databaseUrl
                .map(Url::getShortCode)
                .orElse("");
    }

    public boolean isUrlAlreadyShortened(String urlToCheck) {
        return urlRepository.existsByFullUrl(urlToCheck);
    }

    public boolean isValidUrl(String urlToCheck) {
        try {
            URI uri = new URI(urlToCheck);
            URL url = uri.toURL();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFunctioningUrl(String urlToCheck) {
        try {
            URI uri = URI.create(urlToCheck);
            ResponseEntity<Void> response = restClient.head()
                    .uri(uri)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .retrieve()
                    .toBodilessEntity();

            return response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection();
        } catch (RestClientResponseException e) {
            boolean isBlockedByRobotCheck = e.getStatusCode().value() == 403;
            if (!isBlockedByRobotCheck) {
                logger.info("URL check failed for " + urlToCheck + ": HTTP " + e.getStatusCode().value());
            }
            return isBlockedByRobotCheck; // 403 would indicate that it's blocking us because we are a robot, which is a still functioning url
        } catch (IllegalArgumentException e) {
            logger.info("URL check failed for " + urlToCheck + ": HTTP " + e.toString());
            return false;
        }
    }

    public boolean isSafeUrl(String urlToCheck) {
        try {
            String result = checkUrlWithGoogleSafeBrowsing(urlToCheck);
            return !result.contains("\"matches\"");
        } catch (Exception e) {
            return false;
        }
    }

    private String checkUrlWithGoogleSafeBrowsing(String urlToCheck) throws Exception {
        String endpoint = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + googleSafeBrowsingApiKey;

        Map<String, Object> requestBody = Map.of(
                "client", Map.of(
                        "clientId", "your-company-name",
                        "clientVersion", "1.0.0"
                ),
                "threatInfo", Map.of(
                        "threatTypes", List.of("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"),
                        "platformTypes", List.of("ANY_PLATFORM"),
                        "threatEntryTypes", List.of("URL"),
                        "threatEntries", List.of(Map.of("url", urlToCheck))
                )
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
