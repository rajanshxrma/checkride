package com.checkride.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("api")
@Tag("smoke")
@DisplayName("Smoke: the app is up and wired to its database")
class HealthSmokeTest extends ApiTestBase {

    @Test
    @DisplayName("health endpoint reports UP")
    void healthIsUp() {
        given().spec(anonymous())
                .when().get("/actuator/health")
                .then().statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    @DisplayName("seed data is present: lanes exist")
    void seededLanesExist() {
        given().spec(officer())
                .when().get("/api/v1/lanes")
                .then().statusCode(200)
                .body("size()", org.hamcrest.Matchers.greaterThanOrEqualTo(1));
    }
}
