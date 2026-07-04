package com.checkride.ui.pages;

import com.checkride.support.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LoginPage open() {
        driver.get(Config.baseUrl() + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        return this;
    }

    public void loginAs(String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("signin")).click();
    }

    public String errorBannerText() {
        return wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector(".banner-error"))).getText();
    }

    public String statusBannerText() {
        return wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector(".banner-ok"))).getText();
    }

    public boolean isShown() {
        return !driver.findElements(By.id("signin")).isEmpty();
    }
}
