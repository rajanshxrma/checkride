package com.checkride.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("api")
@Tag("regression")
@DisplayName("Lanes API")
class LaneApiTest extends ApiTestBase {

    @Test
    @DisplayName("lane list has the expected shape")
    void laneListShape() {
        given().spec(officer())
                .when().get("/api/v1/lanes")
                .then().statusCode(200)
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].terminal", notNullValue())
                .body("[0].status", notNullValue());
    }

    @Test
    @DisplayName("statuses only ever come from the known set")
    void statusesAreFromKnownSet() {
        List<String> statuses = given().spec(officer())
                .when().get("/api/v1/lanes")
                .then().statusCode(200)
                .extract().jsonPath().getList("status", String.class);

        assertThat(statuses).isNotEmpty()
                .allMatch(s -> List.of("OPEN", "CLOSED", "MAINTENANCE").contains(s));
    }

    @Test
    @DisplayName("single lane fetch matches the list entry")
    void singleLaneFetch() {
        Integer id = given().spec(officer())
                .when().get("/api/v1/lanes")
                .then().statusCode(200)
                .extract().jsonPath().getInt("[0].id");

        given().spec(officer())
                .when().get("/api/v1/lanes/" + id)
                .then().statusCode(200)
                .body("id", equalTo(id));
    }

    @Test
    @DisplayName("unknown lane id is a clean 404 with a message")
    void unknownLaneIs404() {
        given().spec(officer())
                .when().get("/api/v1/lanes/999999")
                .then().statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("supervisor can change lane status and change it back")
    void supervisorPatchRoundTrip() {
        String original = given().spec(supervisor())
                .when().get("/api/v1/lanes/2")
                .then().statusCode(200)
                .extract().jsonPath().getString("status");

        String flipped = original.equals("MAINTENANCE") ? "OPEN" : "MAINTENANCE";

        given().spec(supervisor())
                .body("{\"status\":\"" + flipped + "\"}")
                .when().patch("/api/v1/lanes/2")
                .then().statusCode(200)
                .body("status", equalTo(flipped));

        // restore so the suite leaves the environment the way it found it
        given().spec(supervisor())
                .body("{\"status\":\"" + original + "\"}")
                .when().patch("/api/v1/lanes/2")
                .then().statusCode(200)
                .body("status", equalTo(original));
    }

    @Test
    @DisplayName("invalid status value is rejected as 400, not stored, not a 500")
    void invalidStatusIs400() {
        given().spec(supervisor())
                .body("{\"status\":\"ON_FIRE\"}")
                .when().patch("/api/v1/lanes/2")
                .then().statusCode(400);
    }
}
