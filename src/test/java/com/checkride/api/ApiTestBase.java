package com.checkride.api;

import com.checkride.support.Config;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public abstract class ApiTestBase {

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.baseURI = Config.baseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected static RequestSpecification anonymous() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    protected static RequestSpecification officer() {
        return basicAuth(Config.officerUser(), Config.officerPassword());
    }

    protected static RequestSpecification supervisor() {
        return basicAuth(Config.supervisorUser(), Config.supervisorPassword());
    }

    protected static RequestSpecification auditor() {
        return basicAuth(Config.auditorUser(), Config.auditorPassword());
    }

    protected static RequestSpecification basicAuth(String user, String password) {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAuth(io.restassured.RestAssured.preemptive().basic(user, password))
                .build();
    }
}
