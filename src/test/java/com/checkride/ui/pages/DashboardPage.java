package com.checkride.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public DashboardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public DashboardPage waitUntilLoaded() {
        wait.until(ExpectedConditions.textToBe(By.cssSelector("main h1"), "Dashboard"));
        return this;
    }

    public String signedInUser() {
        return driver.findElement(By.cssSelector(".who")).getText();
    }

    public boolean hasLogInspectionLink() {
        return !driver.findElements(By.cssSelector("a.nav-cta")).isEmpty();
    }

    public void logout() {
        driver.findElement(By.id("logout")).click();
    }
}
