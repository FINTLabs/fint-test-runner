package no.fint.testrunner.service;

import no.fint.event.model.Event;
import no.fint.event.model.health.Health;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.Status;
import no.fint.testrunner.model.TestRequest;
import no.fint.testrunner.utilities.Headers;
import no.fint.testrunner.utilities.Pwf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthTestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HealthTestValidator healthTestValidator;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    public HealthTestCase runHealthTest(TestRequest testRequest) {

        HttpHeaders headers;
        ResponseEntity<Event<Health>> response;
        String url = String.format("%s%s/admin/health", testRequest.getBaseUrl(), testRequest.getEndpoint());

        if (Pwf.isPwf(testRequest.getBaseUrl())) {
            headers = Headers.createPwfHeaders();
        } else {
            headers = Headers.createHeaders(accessTokenRepository.getAccessToken(testRequest.getClient()).getValue());
        }


        try {
            response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Event<Health>>() {
            });
        } catch (RestClientException e) {
            HealthTestCase healthTestCase = new HealthTestCase();
            healthTestCase.setStatus(Status.FAILED);
            healthTestCase.setMessage(String.format("En feil oppstod under helsesjekken (%s)", e.getCause().getCause().getMessage()));
            return healthTestCase;
        }
        return healthTestValidator.generateStatus(response);


    }

}
