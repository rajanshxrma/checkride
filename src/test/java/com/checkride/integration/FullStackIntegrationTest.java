package com.checkride.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.io.File;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * The heavyweight suite: Testcontainers builds the aerolane image from source,
 * boots app + Postgres on a throwaway network, waits for health, then verifies
 * the deployed stack — not a dev server, the actual shipped containers.
 *
 * Requires the aerolane repo checked out as a sibling directory (../aerolane)
 * and a running Docker daemon. Run with: mvn -Pintegration test
 */
@Tag("integration")
@Testcontainers
@DisplayName("Full stack boots from source and behaves")
class FullStackIntegrationTest {

    @Container
    static final DockerComposeContainer<?> STACK =
            new DockerComposeContainer<>(new File("docker/compose-under-test.yml"))
                    .withLocalCompose(true)
                    .withBuild(true)
                    .withExposedService("app", 8080,
                            Wait.forHttp("/actuator/health").forStatusCode(200)
                                    .withStartupTimeout(Duration.ofMinutes(5)));

    private static String baseUrl() {
        return "http://" + STACK.getServiceHost("app", 8080)
                + ":" + STACK.getServicePort("app", 8080);
    }

    @Test
    @DisplayName("health is UP against real Postgres")
    void healthIsUp() {
        given().baseUri(baseUrl())
                .when().get("/actuator/health")
                .then().statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    @DisplayName("flyway migrated and the seeder ran: lanes exist")
    void seededDataIsPresent() {
        given().baseUri(baseUrl())
                .auth().preemptive().basic("officer", "officer123")
                .when().get("/api/v1/lanes")
                .then().statusCode(200)
                .body("size()", greaterThanOrEqualTo(6));
    }

    @Test
    @DisplayName("role boundaries hold in the deployed stack")
    void rbacHoldsInDeployedStack() {
        given().baseUri(baseUrl())
                .auth().preemptive().basic("officer", "officer123")
                .contentType("application/json")
                .body("{\"status\":\"CLOSED\"}")
                .when().patch("/api/v1/lanes/1")
                .then().statusCode(403);
    }
}
