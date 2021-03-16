package no.fint.testrunner.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.testrunner.model.BasicTestResult;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.service.BasicTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Basic Tests")
@RequestMapping("/api/tests/{orgName}/basic")
public class BasicTestController {

    @Autowired
    private BasicTestService basicTestService;

    @PostMapping
    public ResponseEntity<BasicTestResult> startBasicTest(@PathVariable String orgName,
                                                          @RequestBody TestRequest testRequest) {
        log.info("Starting basic test...");
        BasicTestResult basicTestResult = basicTestService.runBasicTest(orgName, testRequest);
        log.info("Ending basic test...");

        return ResponseEntity.ok().body(basicTestResult);
    }

}
