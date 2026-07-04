package com.checkride.ui.pages;

import com.checkride.support.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InspectionsListPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public InspectionsListPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public InspectionsListPage open() {
        driver.get(Config.baseUrl() + "/inspections");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("main table")));
        return this;
    }

    public String flashText() {
        return wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector(".banner-ok"))).getText();
    }

    public String firstRowEquipment() {
        return driver.findElement(
                By.cssSelector("main table tbody tr:first-child td:nth-child(3)")).getText();
    }

    public String firstRowNotes() {
        return driver.findElement(
                By.cssSelector("main table tbody tr:first-child td:nth-child(5)")).getText();
    }
}
