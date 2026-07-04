package com.checkride.perf;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import com.checkride.support.Config;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;

/**
 * Baseline load: 25 users ramping over 30 seconds doing the two hottest reads.
 * Assertions fail the build if p95 goes past 800ms or more than 1% of
 * requests fail — so "it got slow" is a red build, not a vibe.
 *
 * Run: mvn gatling:test   (report lands in target/gatling)
 */
public class BaselineLoadSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(Config.baseUrl())
            .acceptHeader("application/json");

    ScenarioBuilder readHeavyTraffic = scenario("officer read-heavy traffic")
            .exec(http("list lanes")
                    .get("/api/v1/lanes")
                    .basicAuth(Config.officerUser(), Config.officerPassword())
                    .check(status().is(200)))
            .pause(Duration.ofMillis(250))
            .exec(http("list inspections")
                    .get("/api/v1/inspections")
                    .basicAuth(Config.officerUser(), Config.officerPassword())
                    .check(status().is(200)));

    {
        setUp(readHeavyTraffic.injectOpen(rampUsers(25).during(Duration.ofSeconds(30))))
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(800),
                        global().failedRequests().percent().lt(1.0));
    }
}
