package no.fint.testrunner.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.service.HealthTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Health Tests")
@RequestMapping("/api/tests/health")
public class HealthTestController {

    @Autowired
    private HealthTestService healthTestService;

    @PostMapping
    public ResponseEntity<HealthTestCase> startHealthTest(@RequestBody TestRequest testRequest) {

        log.info("Starting health test...");
        HealthTestCase healthTestCase = healthTestService.runHealthTest(testRequest);
        log.info("Ending health test...");

        return ResponseEntity.ok().body(healthTestCase);
    }

}
