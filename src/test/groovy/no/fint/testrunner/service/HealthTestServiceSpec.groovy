package no.fint.testrunner.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.event.model.Event
import no.fint.event.model.health.Health
import no.fint.event.model.health.HealthStatus
import no.fint.testrunner.model.Status
import no.fint.testrunner.model.TestRequest
import org.springframework.http.MediaType
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

class HealthTestServiceSpec extends Specification {
    private HealthTestService service
    private HealthTestValidator healthTestValidator
    private MockRestServiceServer mockServer

    void setup() {
        AccessTokenRepository accessTokenRepository = Mock() {
            getAccessToken('client') >> Mock(OAuth2AccessToken) {
                getValue() >> 'token'
            }
        }

        def restTemplate = new RestTemplate()
        mockServer = MockRestServiceServer.createServer(restTemplate)

        healthTestValidator = new HealthTestValidator()
        service = new HealthTestService(healthTestValidator: healthTestValidator, accessTokenRepository: accessTokenRepository, restTemplate: restTemplate)
    }

    def "Run health test on non-pwf url with no health data"() {
        given:
        def testRequest = new TestRequest('http://localhost', '/test', 'client')
        def healthEvent = toJson(new Event<Health>())
        mockServer.expect(requestTo('http://localhost/test/admin/health')).andRespond(withSuccess(healthEvent, MediaType.APPLICATION_JSON))

        when:
        def result = service.runHealthTest(testRequest)

        then:
        mockServer.verify()
        result.status == Status.FAILED
    }

    def "Run health test on non-pwf url with healthy response from adapter"() {
        given:
        def testRequest = new TestRequest('http://localhost', '/test', 'client')
        def healthEvent = toJson(new Event<Health>(data: [new Health(status: HealthStatus.APPLICATION_HEALTHY.name())]))
        mockServer.expect(requestTo('http://localhost/test/admin/health')).andRespond(withSuccess(healthEvent, MediaType.APPLICATION_JSON))

        when:
        def result = service.runHealthTest(testRequest)

        then:
        mockServer.verify()
        result.status == Status.OK
    }

    def "Run health test on non-pwf url with unhealthy response from adapter"() {
        given:
        def testRequest = new TestRequest('http://localhost', '/test', 'client')
        def healthEvent = toJson(new Event<Health>(data: [new Health(status: HealthStatus.APPLICATION_UNHEALTHY.name())]))
        mockServer.expect(requestTo('http://localhost/test/admin/health')).andRespond(withSuccess(healthEvent, MediaType.APPLICATION_JSON))

        when:
        def result = service.runHealthTest(testRequest)

        then:
        mockServer.verify()
        result.status == Status.PARTIALLY_FAILED
    }

    private static String toJson(def obj) {
        new ObjectMapper().writeValueAsString(obj)
    }

}
