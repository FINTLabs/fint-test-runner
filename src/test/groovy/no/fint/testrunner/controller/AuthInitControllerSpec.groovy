package no.fint.testrunner.controller

import no.fint.oauth.OAuthRestTemplateFactory
import no.fint.portal.model.client.ClientService
import no.fint.test.utils.MockMvcSpecification
import no.fint.testrunner.service.AccessTokenRepository
import org.springframework.test.web.servlet.MockMvc

class AuthInitControllerSpec extends MockMvcSpecification {
    private AuthInitController controller
    private AccessTokenRepository accessTokenRepository
    private ClientService clientService
    private OAuthRestTemplateFactory templateFactory
    private MockMvc mockMvc

    void setup() {
        accessTokenRepository = Mock()
        clientService = Mock()
        templateFactory = Mock()
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
}
