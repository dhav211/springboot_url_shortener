package com.daniel_havlin.url_shortener_java.services;

import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
import java.util.Random;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final Random random;

    @Value("${googleSafeBrowsingApi.key}")
    private String googleSafeBrowsingApiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UrlService(UrlRepository urlRepository, Random random) {
        this.urlRepository = urlRepository;
        this.random = random;
    }

    public String generateShortCode() {
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

    public boolean isValidUrl(String urlToCheck) {
        try {
            URI uri = new URI(urlToCheck);
            URL url = uri.toURL();

            return true;
        } catch (URISyntaxException | MalformedURLException e){
            return false;
        }
    }

    public boolean isFunctioningUrl(String urlToCheck) {
        try {
            RestClient restClient = RestClient.create();

            ResponseEntity<Void> response = restClient.get()
                    .uri(urlToCheck)
                    .retrieve()
                    .toBodilessEntity();

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSafeUrl(String urlToCheck) {
        try {
            String result = checkUrlWithGoogleSafeBrowsing(urlToCheck);
            System.out.println("RAW RESPONSE: " + result);
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
