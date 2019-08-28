package no.fint.testrunner.controller

import groovy.json.JsonOutput
import no.fint.test.utils.MockMvcSpecification
import no.fint.testrunner.model.HealthTestCase
import no.fint.testrunner.model.TestRequest
import no.fint.testrunner.service.HealthTestService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

class HealthTestControllerSpec extends MockMvcSpecification {
    private HealthTestService service
    private HealthTestController controller
    private MockMvc mockMvc

    void setup() {
        service = Mock()
        controller = new HealthTestController(healthTestService: service)
        mockMvc = standaloneSetup(controller)
    }

    def "Start health test and get test result"() {
        given:
        def request = new TestRequest('http://localhost', '/test', 'client')
        def json = JsonOutput.toJson(request)

        when:
        def response = mockMvc.perform(post('/api/tests/health')
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        1 * service.runHealthTest(request) >> new HealthTestCase(message: 'test result')
        response.andExpect(status().isOk())
                .andExpect(jsonPathEquals('$.message', 'test result'))
    }

}
