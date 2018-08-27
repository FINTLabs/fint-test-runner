package no.fint.testrunner.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.testrunner.model.*;
import no.fint.testrunner.utilities.HttpHeaderService;
import no.fint.testrunner.utilities.Pwf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BasicTestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BasicTestValidator basicTestValidator;

    @Autowired
    private EndpointResourcesService endpointResourcesService;

    @Autowired
    private HttpHeaderService httpHeaderService;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    private HttpHeaders headers;

    public BasicTestResult runBasicTest(TestRequest testRequest) {

        OAuth2AccessToken accessToken = accessTokenRepository.getAccessToken(testRequest.getClient());

        if (Pwf.isPwf(testRequest.getBaseUrl())) {
            headers = httpHeaderService.createPwfHeaders();
        } else {
            headers = httpHeaderService.createHeaders(accessToken.getValue());
        }


        BasicTestResult basicTestResult = new BasicTestResult();

        Optional<List<String>> endpointResources = endpointResourcesService.getEndpointResources(testRequest.getEndpoint());


        if (endpointResources.isPresent()) {
            endpointResources.get().forEach(r -> {
                BasicTestCase basicTestCase = new BasicTestCase();
                basicTestCase.setLastUpdated(getLastUpdated(testRequest, r));
                basicTestCase.setSize(getSize(testRequest, r));
                basicTestCase.setResource(r);
                basicTestValidator.generateStatus(basicTestCase);
                basicTestResult.getCases().add(basicTestCase);
            });
        } else {
            basicTestResult.setMessage(String.format("Sorry but we can't find the service: %s%s", testRequest.getBaseUrl(), testRequest.getEndpoint()));
        }

        return basicTestResult;
    }

    private long getSize(TestRequest testRequest, String resource) {
        String url = String.format("%s/cache/size", testRequest.getTarget(resource));

        log.info(url);

        try {
            ResponseEntity<BasicTestSize> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), BasicTestSize.class);
            return response.getBody().getSize();
        } catch (RestClientException e) {
            return -1;
        }
    }

    private long getLastUpdated(TestRequest testRequest, String resource) {

        String url = String.format("%s/last-updated", testRequest.getTarget(resource));
        log.info(url);

        try {
            ResponseEntity<BasicTestLastUpdated> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), BasicTestLastUpdated.class);
            return response.getBody().getLastUpdated();
        } catch (RestClientException e) {
            return -1;
        }
    }

}
