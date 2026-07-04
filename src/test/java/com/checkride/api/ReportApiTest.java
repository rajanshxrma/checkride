package com.checkride.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("api")
@Tag("regression")
@DisplayName("Reports API")
class ReportApiTest extends ApiTestBase {

    @Test
    @DisplayName("summary math is internally consistent")
    void summaryIsConsistent() {
        JsonPath body = given().spec(auditor())
                .when().get("/api/v1/reports/summary")
                .then().statusCode(200)
                .extract().jsonPath();

        long total = body.getLong("totalInspections");
        long passed = body.getLong("passed");
        long failed = body.getLong("failed");
        double failRate = body.getDouble("failRatePercent");

        assertThat(passed + failed).isEqualTo(total);
        assertThat(failRate).isBetween(0.0, 100.0);
        if (total > 0) {
            assertThat(failRate).isCloseTo(failed * 100.0 / total, org.assertj.core.data.Offset.offset(0.01));
        }
    }

    @Test
    @DisplayName("supervisor can also read the summary")
    void supervisorCanRead() {
        given().spec(supervisor())
                .when().get("/api/v1/reports/summary")
                .then().statusCode(200);
    }
}
