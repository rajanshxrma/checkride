package com.checkride.ui.pages;

import com.checkride.support.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InspectionFormPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public InspectionFormPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public InspectionFormPage open() {
        driver.get(Config.baseUrl() + "/inspections/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("laneId")));
        return this;
    }

    public InspectionFormPage pickFirstLane() {
        new Select(driver.findElement(By.id("laneId"))).selectByIndex(1);
        return this;
    }

    public InspectionFormPage pickEquipment(String equipmentEnumValue) {
        new Select(driver.findElement(By.id("equipment"))).selectByValue(equipmentEnumValue);
        return this;
    }

    public InspectionFormPage chooseResult(String passOrFail) {
        driver.findElement(By.cssSelector("input[name='result'][value='" + passOrFail + "']")).click();
        return this;
    }

    public InspectionFormPage notes(String text) {
        driver.findElement(By.id("notes")).sendKeys(text);
        return this;
    }

    public void submit() {
        // must be the dedicated id: a bare "form button[type=submit]" selector
        // matches the nav's logout button first (learned that one from CI)
        driver.findElement(By.id("save-inspection")).click();
    }
}
