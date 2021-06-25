package no.fint.testrunner.service;

import no.fint.oauth.OAuthRestTemplateFactory;
import no.fint.portal.model.client.Client;
import no.fint.portal.model.client.ClientService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccessTokenService {

    private final ClientService clientService;
    private final OAuthRestTemplateFactory templateFactory;
    private final AccessTokenRepository accessTokenRepository;

    public AccessTokenService(ClientService clientService, OAuthRestTemplateFactory templateFactory, AccessTokenRepository accessTokenRepository) {
        this.clientService = clientService;
        this.templateFactory = templateFactory;
        this.accessTokenRepository = accessTokenRepository;
    }

    public void resetPasswordAndFetchAccessToken(String orgName, String clientName) {
        Client client = clientService.getClient(clientName, orgName).orElseThrow(SecurityException::new);

        String password = UUID.randomUUID().toString().toLowerCase();
        clientService.resetClientPassword(client, password);
        String clientSecret = clientService.getClientSecret(client);

        final OAuth2AccessToken accessToken = fetchAccessToken(client, password, clientSecret);
        accessTokenRepository.addAccessToken(orgName, accessToken);
    }

    @Retryable(include = {OAuth2Exception.class},
            backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000))
    public OAuth2AccessToken fetchAccessToken(Client client, String password, String clientSecret) {
        OAuth2RestTemplate oAuth2RestTemplate = templateFactory.create(client.getName(), password, client.getClientId(), clientSecret);
        return oAuth2RestTemplate.getAccessToken();
    }
}
