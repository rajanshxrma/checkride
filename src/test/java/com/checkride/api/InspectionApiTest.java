package com.checkride.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("api")
@Tag("regression")
@DisplayName("Inspections API")
class InspectionApiTest extends ApiTestBase {

    @Test
    @DisplayName("officer creates an inspection: 201, Location header, server-side attribution")
    void createRoundTrip() {
        Integer id = given().spec(officer())
                .body("""
                        {
                          "laneId": 1,
                          "equipment": "METAL_DETECTOR",
                          "result": "PASS",
                          "notes": "checkride api suite"
                        }
                        """)
                .when().post("/api/v1/inspections")
                .then().statusCode(201)
                .header("Location", containsString("/api/v1/inspections/"))
                .body("inspectedBy", equalTo(com.checkride.support.Config.officerUser()))
                .extract().jsonPath().getInt("id");

        given().spec(officer())
                .when().get("/api/v1/inspections/" + id)
                .then().statusCode(200)
                .body("equipment", equalTo("METAL_DETECTOR"))
                .body("notes", equalTo("checkride api suite"));
    }

    @Test
    @DisplayName("missing required fields come back as per-field validation errors")
    void missingFieldsAreFieldErrors() {
        given().spec(officer())
                .body("{\"notes\":\"missing everything else\"}")
                .when().post("/api/v1/inspections")
                .then().statusCode(400)
                .body("fieldErrors.laneId", notNullValue())
                .body("fieldErrors.equipment", notNullValue())
                .body("fieldErrors.result", notNullValue());
    }

    @Test
    @DisplayName("unknown enum value is a 400 with guidance, not a 500")
    void unknownEnumIs400() {
        given().spec(officer())
                .body("{\"laneId\":1,\"equipment\":\"TELEPORTER\",\"result\":\"PASS\"}")
                .when().post("/api/v1/inspections")
                .then().statusCode(400)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("inspection against a lane that doesn't exist is a 404")
    void unknownLaneIs404() {
        given().spec(officer())
                .body("{\"laneId\":999999,\"equipment\":\"XRAY_SCANNER\",\"result\":\"PASS\"}")
                .when().post("/api/v1/inspections")
                .then().statusCode(404);
    }

    @Test
    @DisplayName("oversized notes are rejected by validation")
    void oversizedNotesAre400() {
        String longNotes = "x".repeat(501);
        given().spec(officer())
                .body("{\"laneId\":1,\"equipment\":\"XRAY_SCANNER\",\"result\":\"PASS\",\"notes\":\"" + longNotes + "\"}")
                .when().post("/api/v1/inspections")
                .then().statusCode(400)
                .body("fieldErrors.notes", notNullValue());
    }

    @Test
    @DisplayName("result filter returns only matching records")
    void resultFilterIsExact() {
        given().spec(supervisor())
                .when().get("/api/v1/inspections?result=FAIL")
                .then().statusCode(200)
                .body("result", everyItem(equalTo("FAIL")));
    }

    @Test
    @DisplayName("invalid filter value is a 400, not a 500")
    void invalidFilterIs400() {
        given().spec(supervisor())
                .when().get("/api/v1/inspections?result=MAYBE")
                .then().statusCode(400);
    }
}
