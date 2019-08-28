package no.fint.testrunner.controller

import groovy.json.JsonOutput
import no.fint.test.utils.MockMvcSpecification
import no.fint.testrunner.model.BasicTestResult
import no.fint.testrunner.model.TestRequest
import no.fint.testrunner.service.BasicTestService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

class BasicTestControllerSpec extends MockMvcSpecification {
    private MockMvc mockMvc
    private BasicTestController controller
    private BasicTestService service

    void setup() {
        service = Mock()
        controller = new BasicTestController(basicTestService: service)
        mockMvc = standaloneSetup(controller)
    }

    def "Start basic test and get test result"() {
        given:
        def request = new TestRequest('http://localhost', '/test', 'client')
        def json = JsonOutput.toJson(request)

        when:
        def response = mockMvc.perform(post('/api/tests/basic')
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        1 * service.runBasicTest(request) >> new BasicTestResult(message: 'test result')
        response.andExpect(status().isOk())
                .andExpect(jsonPathEquals('$.message', 'test result'))
    }
}
