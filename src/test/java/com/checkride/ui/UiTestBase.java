package com.checkride.ui;

import com.checkride.support.Config;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class UiTestBase {

    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * Failure forensics: when any UI test fails, capture the current URL, the
     * full page source and a screenshot into target/ui-failures/ before the
     * browser is torn down. CI uploads that directory as an artifact, so a red
     * build comes with evidence instead of a bare stack trace.
     */
    @RegisterExtension
    final AfterTestExecutionCallback failureForensics = context -> {
        if (context.getExecutionException().isPresent() && driver != null) {
            String name = context.getRequiredTestClass().getSimpleName()
                    + "-" + context.getRequiredTestMethod().getName();
            try {
                Path dir = Path.of("target", "ui-failures");
                Files.createDirectories(dir);
                Files.writeString(dir.resolve(name + ".url.txt"), driver.getCurrentUrl(),
                        StandardCharsets.UTF_8);
                Files.writeString(dir.resolve(name + ".html"), driver.getPageSource(),
                        StandardCharsets.UTF_8);
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Files.write(dir.resolve(name + ".png"), png);
            } catch (Exception e) {
                System.err.println("Could not capture failure evidence for " + name + ": " + e);
            }
        }
    };

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu",
                "--window-size=1400,1000");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
