package no.fint.testrunner.service;

import no.fint.event.model.Event;
import no.fint.event.model.health.Health;
import no.fint.oauth.TokenService;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.utilities.HttpHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthTestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired
    HealthTestValidator healthTestValidator;

    @Autowired
    HttpHeaderService httpHeaderService;

    public HealthTestCase runHealthTest(TestRequest testRequest) {

        HttpHeaders headers = httpHeaderService.createHeaders(testRequest, tokenService);

        String url = String.format("%s%s/admin/health", testRequest.getBaseUrl(), testRequest.getEndpoint());

        ResponseEntity<Event<Health>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Event<Health>>(){});

        return healthTestValidator.generateStatus(response.getBody());

    }


}
