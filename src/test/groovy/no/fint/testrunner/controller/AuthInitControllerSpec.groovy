package no.fint.testrunner.controller

import groovy.json.JsonOutput
import no.fint.oauth.OAuthRestTemplateFactory
import no.fint.portal.model.client.Client
import no.fint.portal.model.client.ClientService
import no.fint.test.utils.MockMvcSpecification
import no.fint.testrunner.model.TestRequest
import no.fint.testrunner.service.AccessTokenRepository
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.test.web.servlet.MockMvc

class AuthInitControllerSpec extends MockMvcSpecification {
    private AuthInitController controller
    private AccessTokenRepository accessTokenRepository
    private ClientService clientService
    private OAuthRestTemplateFactory templateFactory
    private OAuth2RestTemplate restTemplate
    private MockMvc mockMvc

    void setup() {
        accessTokenRepository = Mock()
        clientService = Mock()
        templateFactory = Mock()
        restTemplate = Mock()
        controller = new AuthInitController(
                accessTokenRepository: accessTokenRepository,
                clientService: clientService,
                templateFactory: templateFactory
        )
        mockMvc = standaloneSetup(controller)
    }

    def "Clear authorizations"() {
        when:
        def response = mockMvc.perform(get('/api/tests/auth/clear/orgName'))

        then:
        1 * accessTokenRepository.clearAccessTokens('orgName')
        response.andExpect(status().isNoContent())
    }

    def 'Auth init'() {
        when:
        def request = new TestRequest(baseUrl: '/base', endpoint: '/endpoint', client: 'client')
        def response = mockMvc.perform(post('/api/tests/auth/init')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JsonOutput.toJson(request)))

        then:
        1 * clientService.getClientByDn('client') >> Optional.of(new Client(name: 'name', clientId: 'client-id'))
        1 * clientService.getClientSecret(_ as Client) >> 'very-secret'
        1 * templateFactory.create('name', _ as String, 'client-id', 'very-secret') >> restTemplate
        1 * restTemplate.getAccessToken() >> Mock(OAuth2AccessToken)
        1 * accessTokenRepository.addAccessToken('client', _ as OAuth2AccessToken)
        response.andExpect(status().isNoContent())
    }
}
