package com.codegik;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ActuatorHealthCheckerSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://127.0.0.1:8280")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .doNotTrackHeader("1")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");

    ScenarioBuilder scn = scenario(ActuatorHealthCheckerSimulation.class.getSimpleName())
            .exec(http("request")
                    .get("/actuator/health"))
            .pause(1);

    {
        setUp(scn
                .injectOpen(
                        constantUsersPerSec(400).during(180)
                )
        ).protocols(httpProtocol);
    }
}