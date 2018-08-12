package no.fint.testrunner.utilities;

import no.fint.oauth.TokenService;
import no.fint.portal.model.client.Client;
import no.fint.portal.model.client.ClientService;
import no.fint.testrunner.model.TestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HttpHeaderService {

    @Autowired
    private ClientService clientService;

    public HttpHeaders createHeaders(TestRequest testRequest, TokenService tokenService) {

        HttpHeaders headers = new HttpHeaders();
        Optional<Client> client = clientService.getClientByDn(testRequest.getClient());

        if (isPwf(testRequest.getBaseUrl())) {
            headers.set("x-org-id", "pwf.no" /*testRequest.getOrgId()*/);
        }
        else {
            client.ifPresent(c -> {
                headers.set("x-org-id", c.getAssetId());
            });
        }
        headers.set("x-client", testRequest.getClient());
        if (tokenService != null) {
            headers.set("Authorization", String.format("Bearer %s", tokenService.getAccessToken()));
        }

        return headers;
    }

    private boolean isPwf(String baseUrl) {
        return baseUrl.equals("https://play-with-fint.felleskomponent.no");
    }

}
