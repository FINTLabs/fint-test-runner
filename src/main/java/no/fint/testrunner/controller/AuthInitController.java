package no.fint.testrunner.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.fint.oauth.OAuthRestTemplateFactory;
import no.fint.portal.model.client.Client;
import no.fint.portal.model.client.ClientService;
import no.fint.testrunner.model.AccessTokenRepository;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.utilities.Pwf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Basic Tests")
@RequestMapping("/api/tests/auth")

public class AuthInitController {

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    private OAuthRestTemplateFactory factory;

    @PostMapping("/init")
    public ResponseEntity<Void> authorize(@RequestBody TestRequest testRequest) {

        if (!Pwf.isPwf(testRequest.getBaseUrl())) {
            boolean isExpired = Optional.ofNullable(accessTokenRepository.getAccessToken(testRequest.getClient())).map(OAuth2AccessToken::isExpired).orElse(true);
            if (!isExpired) {
                return ResponseEntity.noContent().build();
            }

            Client client = clientService.getClientByDn(testRequest.getClient()).orElseThrow(SecurityException::new);

            String password = UUID.randomUUID().toString().toLowerCase();
            clientService.resetClientPassword(client, password);
            String clientSecret = clientService.getClientSecret(client);

            OAuth2RestTemplate oAuth2RestTemplate = factory.create(client.getName(), password, client.getClientId(), clientSecret);

            accessTokenRepository.addAccessToken(testRequest.getClient(), oAuth2RestTemplate.getAccessToken());

            System.out.println("accessTokenRepository.getAccessToken() = " + accessTokenRepository.getAccessToken(testRequest.getClient()));
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity clientNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sorry, men vi finner ikke klienten du valgte!");
    }

    @GetMapping("/clear/{orgName}")
    public ResponseEntity<Void> clearAuthorizations(@PathVariable String orgName) {
        accessTokenRepository.clearAccessTokens(orgName);

        return ResponseEntity.noContent().build();
    }
}
