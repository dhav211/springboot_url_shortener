package com.daniel_havlin.url_shortener_java.integration;

import com.daniel_havlin.url_shortener_java.services.UrlService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
public class HomePageTests {
    @LocalServerPort
    int port;

    private WebDriver driver;
    private HomePageObjectModel homePageObjectModel;


    @BeforeEach
    void setup() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        homePageObjectModel = new HomePageObjectModel(driver, port);
    }

    @Test
    void homePageHasCorrectTitle() {
        assertEquals("YaUS!! Home!!", homePageObjectModel.getPageTitle());
    }

    @Test
    void urlSuccessfullyShortened() {
        homePageObjectModel.submitUrl("https://www.google.com");

        assertEquals(6, homePageObjectModel.getSuccessfulShortCodeText().length());
    }

    @Test
    void redirectOnSuccessfullyShortenedUrl() {
        String urlToShorten = "https://en.wikipedia.org/wiki/New_York_(magazine)";
        homePageObjectModel.submitUrl(urlToShorten);

        String shortCode = homePageObjectModel.getSuccessfulShortCodeText();
        homePageObjectModel.waitForUrlToLoad(shortCode, urlToShorten, port);

        assertEquals(urlToShorten, driver.getCurrentUrl());
    }

    @Test
    void failedToCreateShortCodeOnAlreadyShortenedUrl() {
        String urlToShorten = "https://en.wikipedia.org/wiki/New_York_(magazine)";
        String failureMessage = "We've already shortened that url, you can check it out at http://localhost/";
        homePageObjectModel.submitUrl(urlToShorten);
        // The banner will pop open giving the user the success message, but we can grab the short code from there
        String shortCode = homePageObjectModel.getSuccessfulShortCodeText();

        // Reload the page and try the same url again to get an error
        driver.get("http://localhost:" + port);
        homePageObjectModel.submitUrl(urlToShorten);
        String error = homePageObjectModel.waitForErrorMessage();

        assertEquals(failureMessage + shortCode, error);
    }

    @Test
    void failedToCreateShortCodeOnMalformedUrl() {
        String failureMessage = "You've given an invalid url, try it in your browser and correct any mistakes.";
        String malformedUrl = "htps://google.com";
        homePageObjectModel.submitUrl(malformedUrl);
        String error = homePageObjectModel.waitForErrorMessage();

        assertEquals(failureMessage, error);

    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
