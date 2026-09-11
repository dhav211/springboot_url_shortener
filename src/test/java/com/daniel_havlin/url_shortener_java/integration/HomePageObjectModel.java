package com.daniel_havlin.url_shortener_java.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePageObjectModel {
    protected WebDriver driver;
    protected int port;

    // <input type="text" id="url" name="url" placeholder="What will you shorten today?" />
    private final By urlBarBy = By.name("url");
    // <button id="shorten-button" type="submit">
    private final By shortenButtonBy = By.id("shorten-button");
    // <div id="banner">
    private final By bannerBy = By.id("banner");
    private final By shortenedUrlLinkBy = By.id("shorten-url-address-link");
    private final By errorMessageBy = By.id("error-message");

    public HomePageObjectModel(WebDriver driver, int port){
        this.driver = driver;
        this.port = port;
        driver.get("http://localhost:" + port);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public void submitUrl(String address) {
        driver.findElement(urlBarBy).sendKeys(address);
        driver.findElement(shortenButtonBy).click();
    }

    private WebElement waitForShortCodeLink() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(shortenedUrlLinkBy));
    }

    public String getSuccessfulShortCodeText() {
        WebElement shortCodeLink = waitForShortCodeLink();
        return shortCodeLink.getText();
    }

    public void waitForUrlToLoad(String shortCode, String url, int port) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:" + port + "/" + shortCode);
        wait.until(ExpectedConditions.urlToBe(url));
    }

    public String waitForErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageBy));
        return errorMessage.getText();
    }
}
