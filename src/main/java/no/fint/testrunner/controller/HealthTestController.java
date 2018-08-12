package no.fint.testrunner.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.health.Health;
import no.fint.testrunner.model.BasicTestResult;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.HealthTestResult;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.service.BasicTestService;
import no.fint.testrunner.service.HealthTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Basic Tests")
@RequestMapping("/api/tests/health")
public class HealthTestController {

    @Autowired
    HealthTestService healthTestService;

    @PostMapping
    public ResponseEntity<HealthTestCase> startHealthTest(@RequestBody TestRequest testRequest) {

        log.info("Starting basic test...");
        HealthTestCase healthTestCase = healthTestService.runHealthTest(testRequest);
        log.info("Ending basic test...");

        return ResponseEntity.ok().body(healthTestCase);
    }

}
