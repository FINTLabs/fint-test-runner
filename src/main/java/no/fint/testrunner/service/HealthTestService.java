package no.fint.testrunner.service;

import no.fint.event.model.Event;
import no.fint.event.model.health.Health;
import no.fint.testrunner.model.AccessTokenRepository;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.utilities.HttpHeaderService;
import no.fint.testrunner.utilities.Pwf;
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

    @Autowired
    private HealthTestValidator healthTestValidator;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private HttpHeaderService httpHeaderService;

    public HealthTestCase runHealthTest(TestRequest testRequest) {

        HttpHeaders headers;
        ResponseEntity<Event<Health>> response;
        String url = String.format("%s%s/admin/health", testRequest.getBaseUrl(), testRequest.getEndpoint());

        if (Pwf.isPwf(testRequest.getBaseUrl())) {
            headers = httpHeaderService.createPwfHeaders();
        } else {
            headers = httpHeaderService.createHeaders(accessTokenRepository.getAccessToken(testRequest.getClient()).getValue());
        }


        response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Event<Health>>() {
        });
        return healthTestValidator.generateStatus(response.getBody());


    }

}
