package com.daniel_havlin.url_shortener_java;

import com.daniel_havlin.url_shortener_java.models.Url;
import com.daniel_havlin.url_shortener_java.repositories.UrlRepository;
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

    static WebDriver driver;

    @MockitoBean
    private UrlService urlService;

    @BeforeAll
    static void setup() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
    }

    @Test
    void homePageHasCorrectTitle() {
        driver.get("http://localhost:" + port);
        assertEquals("YaUS!!", driver.getTitle());
    }

    @Test
    void urlSuccessfullyShortened() {
        driver.get("http://localhost:" + port);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        when(urlService.isUrlAlreadyShortened(anyString())).thenReturn(false);
        when(urlService.isValidUrl(anyString())).thenReturn(true);
        when(urlService.isFunctioningUrl(anyString())).thenReturn(true);
        when(urlService.isSafeUrl(anyString())).thenReturn(true);
        when(urlService.generateShortCode()).thenReturn("abc123");

        WebElement urlInput = driver.findElement(By.id("url"));
        urlInput.sendKeys("https://www.google.com");

        WebElement submitButton = driver.findElement(By.id("shorten-button"));
        submitButton.click();

        WebElement shortCode = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("shorten-url-address-link")));
        assertEquals("abc123", shortCode.getText());
    }

    @AfterAll
    static void teardown() {
        driver.quit();
    }
}
