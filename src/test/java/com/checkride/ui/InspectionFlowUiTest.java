package com.checkride.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkride.support.Config;
import com.checkride.ui.pages.DashboardPage;
import com.checkride.ui.pages.InspectionFormPage;
import com.checkride.ui.pages.InspectionsListPage;
import com.checkride.ui.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("ui")
@Tag("regression")
@DisplayName("End-to-end: log an inspection through the UI")
class InspectionFlowUiTest extends UiTestBase {

    @Test
    @DisplayName("officer logs a failed check and it shows up at the top of the list")
    void officerLogsInspection() {
        String marker = "checkride-ui-" + System.currentTimeMillis();

        new LoginPage(driver, wait).open()
                .loginAs(Config.officerUser(), Config.officerPassword());
        new DashboardPage(driver, wait).waitUntilLoaded();

        new InspectionFormPage(driver, wait).open()
                .pickFirstLane()
                .pickEquipment("BAGGAGE_CONVEYOR")
                .chooseResult("FAIL")
                .notes(marker)
                .submit();

        // if the browser blocked or the server rejected the submit, we're still
        // on /inspections/new — fail HERE and say exactly why: url + the
        // validation errors the page is showing
        try {
            wait.until(d -> !d.getCurrentUrl().contains("/inspections/new"));
        } catch (org.openqa.selenium.TimeoutException e) {
            String fieldErrors = driver.findElements(org.openqa.selenium.By.cssSelector(".field-error"))
                    .stream().map(org.openqa.selenium.WebElement::getText)
                    .reduce("", (a, b) -> a + " | " + b);
            throw new AssertionError("form did not submit; still on " + driver.getCurrentUrl()
                    + "; field errors:" + fieldErrors, e);
        }

        InspectionsListPage list = new InspectionsListPage(driver, wait);
        assertThat(list.flashText()).contains("Inspection logged");
        assertThat(list.firstRowEquipment()).isEqualTo("BAGGAGE CONVEYOR");
        assertThat(list.firstRowNotes()).contains(marker);
    }
}
