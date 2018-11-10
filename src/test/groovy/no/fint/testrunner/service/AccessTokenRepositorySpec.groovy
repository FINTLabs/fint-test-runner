package no.fint.testrunner.service

import org.springframework.security.oauth2.common.OAuth2AccessToken
import spock.lang.Specification

class AccessTokenRepositorySpec extends Specification {
    private AccessTokenRepository repository

    void setup() {
        repository = new AccessTokenRepository()
        repository.init()
    }

    def "Add and get access token"() {
        given:
        OAuth2AccessToken token = Mock()

        when:
        repository.addAccessToken('key', token)
        def storedToken = repository.getAccessToken('key')

        then:
        token == storedToken
    }

    def "Clear access tokens"() {
        given:
        repository.addAccessToken('key1', Mock(OAuth2AccessToken))

        when:
        repository.clearAccessTokens('key1')
        def token = repository.getAccessToken('key1')

        then:
        !token
    }
}
