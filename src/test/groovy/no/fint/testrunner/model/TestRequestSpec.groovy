package no.fint.testrunner.model

import spock.lang.Specification

class TestRequestSpec extends Specification {

    def "Get target given empty baseUrl returns url with default base"() {
        when:
        def target = new TestRequest('', '/test', 'client').getTarget('resource')

        then:
        target == "${TestRequest.DEFAULT_BASE_URL}/test/resource"
    }

    def "Get target given baseUrl returns url"() {
        when:
        def target = new TestRequest('http://localhost', '/test', 'client').getTarget('resource')

        then:
        target == 'http://localhost/test/resource'
    }
}
