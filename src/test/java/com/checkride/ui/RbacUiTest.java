package com.checkride.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkride.support.Config;
import com.checkride.ui.pages.DashboardPage;
import com.checkride.ui.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Role boundaries checked the way a user would actually hit them:
 * what's visible in the UI, and what happens when someone bypasses the UI
 * and types a privileged URL directly into the address bar.
 */
@Tag("ui")
@Tag("regression")
@DisplayName("Role-based access in the browser")
class RbacUiTest extends UiTestBase {

    @Test
    @DisplayName("officer sees the 'Log inspection' action")
    void officerSeesLogInspection() {
        new LoginPage(driver, wait).open()
                .loginAs(Config.officerUser(), Config.officerPassword());
        DashboardPage dashboard = new DashboardPage(driver, wait).waitUntilLoaded();

        assertThat(dashboard.hasLogInspectionLink()).isTrue();
    }

    @Test
    @DisplayName("auditor doesn't see the 'Log inspection' action")
    void auditorDoesNotSeeLogInspection() {
        new LoginPage(driver, wait).open()
                .loginAs(Config.auditorUser(), Config.auditorPassword());
        DashboardPage dashboard = new DashboardPage(driver, wait).waitUntilLoaded();

        assertThat(dashboard.hasLogInspectionLink()).isFalse();
    }

    @Test
    @DisplayName("hiding the button isn't the control: direct URL access is denied server-side")
    void auditorDirectUrlIsForbidden() {
        new LoginPage(driver, wait).open()
                .loginAs(Config.auditorUser(), Config.auditorPassword());
        new DashboardPage(driver, wait).waitUntilLoaded();

        driver.get(Config.baseUrl() + "/inspections/new");
        assertThat(driver.getPageSource()).contains("403");
    }
}
