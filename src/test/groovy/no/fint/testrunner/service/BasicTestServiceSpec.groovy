package no.fint.testrunner.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.testrunner.model.BasicTestLastUpdated
import no.fint.testrunner.model.BasicTestSize
import no.fint.testrunner.model.TestRequest
import org.springframework.http.MediaType
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

class BasicTestServiceSpec extends Specification {
    private BasicTestService service
    private EndpointResourcesService endpointResourcesService
    private MockRestServiceServer mockServer

    void setup() {
        AccessTokenRepository accessTokenRepository = Mock() {
            getAccessToken('client') >> Mock(OAuth2AccessToken) {
                getValue() >> 'token'
            }
        }

        def restTemplate = new RestTemplate()
        mockServer = MockRestServiceServer.createServer(restTemplate)

        endpointResourcesService = Mock()
        service = new BasicTestService(accessTokenRepository: accessTokenRepository, endpointResourcesService: endpointResourcesService, restTemplate: restTemplate)
    }

    def "Run basic test on non-pwf url"() {
        given:
        def lastUpdated = new ObjectMapper().writeValueAsString(new BasicTestLastUpdated(lastUpdated: 123456L))
        mockServer.expect(requestTo('http://localhost/test/test-resource/last-updated')).andRespond(withSuccess(lastUpdated, MediaType.APPLICATION_JSON))

        def cacheSize = new ObjectMapper().writeValueAsString(new BasicTestSize(size: 5))
        mockServer.expect(requestTo('http://localhost/test/test-resource/cache/size')).andRespond(withSuccess(cacheSize, MediaType.APPLICATION_JSON))
        def testRequest = new TestRequest('http://localhost', '/test', 'client')

        when:
        def result = service.runBasicTest(testRequest)

        then:
        1 * endpointResourcesService.getEndpointResources('/test') >> Optional.of(['test-resource'])
        mockServer.verify()
        result.cases.size() == 1
    }

}
