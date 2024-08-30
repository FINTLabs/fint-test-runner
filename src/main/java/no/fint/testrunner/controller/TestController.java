package no.fint.testrunner.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Ping")
@RequestMapping("/ping")
public class TestController {

    @GetMapping
    public ResponseEntity<String> testGet() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping
    public void testPost() {
        log.info("ok");
    }

}
