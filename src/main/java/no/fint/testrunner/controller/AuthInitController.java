package no.fint.testrunner.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.service.AccessTokenRepository;
import no.fint.testrunner.service.AccessTokenService;
import no.fint.testrunner.utilities.Pwf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Basic Tests")
@RequestMapping("/api/tests/{orgName}/auth")
public class AuthInitController {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private AccessTokenService accessTokenService;

    @PostMapping("/init/{clientName}")
    public ResponseEntity<Void> authorize(@PathVariable String orgName,
                                          @PathVariable String clientName,
                                          @RequestBody TestRequest testRequest) {

        if (!Pwf.isPwf(testRequest.getBaseUrl())) {
            log.info("Auth Init, {}, {}", orgName, clientName);
            boolean isExpired = Optional.ofNullable(accessTokenRepository.getAccessToken(orgName)).map(OAuth2AccessToken::isExpired).orElse(true);
            if (!isExpired) {
                return ResponseEntity.noContent().build();
            }

            accessTokenService.resetPasswordAndFetchAccessToken(orgName, clientName);
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity clientNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sorry, men vi finner ikke klienten du valgte!");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearAuthorizations(@PathVariable String orgName) {
        accessTokenRepository.deleteAccessToken(orgName);
        return ResponseEntity.noContent().build();
    }
}
