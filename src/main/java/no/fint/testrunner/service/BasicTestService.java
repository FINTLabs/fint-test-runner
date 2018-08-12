package no.fint.testrunner.service;

import no.fint.oauth.TokenService;
import no.fint.testrunner.model.*;
import no.fint.testrunner.utilities.HttpHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class BasicTestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired
    private BasicTestValidator basicTestValidator;

    @Autowired
    private EndpointResourcesService endpointResourcesService;

    @Autowired
    HttpHeaderService httpHeaderService;


    public BasicTestResult runBasicTest(TestRequest testRequest) {

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
        HttpHeaders headers = httpHeaderService.createHeaders(testRequest, tokenService);
        String url = String.format("%s/cache/size", testRequest.getTarget(resource));
        ResponseEntity<BasicTestSize> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), BasicTestSize.class);

        return response.getBody().getSize();
    }

    private long getLastUpdated(TestRequest testRequest, String resource) {

        HttpHeaders headers = httpHeaderService.createHeaders(testRequest, tokenService);
        String url = String.format("%s/last-updated", testRequest.getTarget(resource));

        ResponseEntity<BasicTestLastUpdated> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), BasicTestLastUpdated.class);

        return response.getBody().getLastUpdated();
    }

    /*
    private HttpHeaders createHeaders(TestRequest testRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-org-id", "pwf.no");
        headers.set("x-client", testRequest.getClient());
        if (tokenService != null) {
            headers.set("Authorization", String.format("Bearer %s", tokenService.getAccessToken()));
        }

        return headers;
    }
    */


}
