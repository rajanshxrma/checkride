package com.checkride.api;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The authentication/authorization contract of the API:
 * missing creds → 401, wrong creds → 401, valid creds without the right
 * role → 403. These are separate failure modes and the API must not blur them.
 */
@Tag("api")
@Tag("smoke")
@DisplayName("Auth contract: 401 vs 403 semantics")
class AuthContractTest extends ApiTestBase {

    @Test
    @DisplayName("no credentials is 401, not a redirect")
    void anonymousIsUnauthorized() {
        given().spec(anonymous())
                .when().get("/api/v1/lanes")
                .then().statusCode(401);
    }

    @Test
    @DisplayName("wrong password is 401")
    void wrongPasswordIsUnauthorized() {
        given().spec(basicAuth(com.checkride.support.Config.officerUser(), "definitely-wrong"))
                .when().get("/api/v1/lanes")
                .then().statusCode(401);
    }

    @Test
    @DisplayName("officer can read lanes")
    void officerCanRead() {
        given().spec(officer())
                .when().get("/api/v1/lanes")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("officer cannot change lane status (role boundary)")
    void officerCannotPatchLane() {
        given().spec(officer())
                .body("{\"status\":\"CLOSED\"}")
                .when().patch("/api/v1/lanes/1")
                .then().statusCode(403);
    }

    @Test
    @DisplayName("officer cannot read auditor reports (role boundary)")
    void officerCannotReadReports() {
        given().spec(officer())
                .when().get("/api/v1/reports/summary")
                .then().statusCode(403);
    }

    @Test
    @DisplayName("auditor cannot write inspections (role boundary)")
    void auditorCannotCreateInspection() {
        given().spec(auditor())
                .body("{\"laneId\":1,\"equipment\":\"XRAY_SCANNER\",\"result\":\"PASS\"}")
                .when().post("/api/v1/inspections")
                .then().statusCode(403);
    }
}
