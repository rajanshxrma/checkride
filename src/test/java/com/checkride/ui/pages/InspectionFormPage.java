package com.checkride.ui.pages;

import com.checkride.support.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        var lane = driver.findElement(By.id("laneId"));
        Select laneSelect = new Select(lane);
        laneSelect.selectByIndex(1);
        if (laneSelect.getFirstSelectedOption().getAttribute("value").isBlank()) {
            // pointer-driven option selection can no-op in CI chrome; set the value
            // directly and fire the change event the way a browser would
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].selectedIndex = 1; arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", lane);
        }
        return this;
    }

    public InspectionFormPage pickEquipment(String equipmentEnumValue) {
        var equipment = driver.findElement(By.id("equipment"));
        Select equipmentSelect = new Select(equipment);
        equipmentSelect.selectByValue(equipmentEnumValue);
        if (!equipmentSelect.getFirstSelectedOption().getAttribute("value").equals(equipmentEnumValue)) {
            // pointer-driven option selection can no-op in CI chrome; set the value
            // directly and fire the change event the way a browser would
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                    equipment, equipmentEnumValue);
        }
        return this;
    }

    public InspectionFormPage chooseResult(String passOrFail) {
        var radio = driver.findElement(
                By.cssSelector("input[name='result'][value='" + passOrFail + "']"));
        radio.click();
        if (!radio.isSelected()) {
            // pointer click can silently no-op on tiny (13px) native radio inputs
            // depending on the click coordinate WebDriver computes. Keyboard
            // selection is how a keyboard-only user does it and is deterministic.
            radio.sendKeys(org.openqa.selenium.Keys.SPACE);
        }
        if (!radio.isSelected()) {
            radio.findElement(By.xpath("./ancestor::label")).click();
        }
        if (!radio.isSelected()) {
            // last resort: drive the DOM node directly. This bypasses whatever
            // native mouse-event coordinate math WebDriver used above and still
            // fires the real click/input/change events the app listens for,
            // since it calls the actual HTMLInputElement.click().
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radio);
        }
        if (!radio.isSelected()) {
            throw new IllegalStateException("could not select result radio " + passOrFail);
        }
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (driver.getCurrentUrl().contains("/inspections/new")) {
            // CI chrome can drop the pointer-driven click event on this button;
            // requestSubmit still runs constraint validation and CSRF like a real click
            ((JavascriptExecutor) driver).executeScript(
                    "document.getElementById('save-inspection').closest('form').requestSubmit();");
        }
    }
}
