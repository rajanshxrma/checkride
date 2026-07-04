package com.checkride.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkride.support.Config;
import com.checkride.ui.pages.DashboardPage;
import com.checkride.ui.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("ui")
@Tag("smoke")
@DisplayName("Login flow in a real browser")
class LoginUiTest extends UiTestBase {

    @Test
    @DisplayName("wrong credentials show an error and keep you on the login page")
    void wrongCredentialsAreRejected() {
        LoginPage login = new LoginPage(driver, wait).open();
        login.loginAs(Config.officerUser(), "not-the-password");

        assertThat(login.errorBannerText()).contains("Wrong username or password");
        assertThat(login.isShown()).isTrue();
    }

    @Test
    @DisplayName("officer signs in and lands on the dashboard")
    void officerSignsIn() {
        new LoginPage(driver, wait).open()
                .loginAs(Config.officerUser(), Config.officerPassword());

        DashboardPage dashboard = new DashboardPage(driver, wait).waitUntilLoaded();
        assertThat(dashboard.signedInUser()).isEqualTo(Config.officerUser());
    }

    @Test
    @DisplayName("logout kills the session — back button access doesn't work")
    void logoutEndsSession() {
        new LoginPage(driver, wait).open()
                .loginAs(Config.officerUser(), Config.officerPassword());
        DashboardPage dashboard = new DashboardPage(driver, wait).waitUntilLoaded();

        dashboard.logout();
        LoginPage login = new LoginPage(driver, wait);
        assertThat(login.statusBannerText()).contains("Signed out");

        // hitting a protected page after logout must bounce to login
        driver.get(Config.baseUrl() + "/inspections");
        assertThat(driver.getCurrentUrl()).contains("/login");
    }
}
