package no.fint.testrunner.controller

import no.fint.test.utils.MockMvcSpecification
import no.fint.testrunner.model.TestRequest
import no.fint.testrunner.service.BasicTestService
import org.codehaus.jackson.map.ObjectMapper
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

    def "Start basic test"() {
        given:
        def request = new TestRequest('http://localhost', '/test', 'client')
        def json = new ObjectMapper().writeValueAsString(request)

        when:
        def response = mockMvc.perform(post('/api/tests/basic')
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

        then:
        response.andExpect(status().isOk())
    }
}
