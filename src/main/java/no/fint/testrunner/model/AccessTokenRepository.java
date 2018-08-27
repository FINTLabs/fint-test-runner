package no.fint.testrunner.model;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class AccessTokenRepository {
    private ConcurrentMap<String, OAuth2AccessToken> tokenCache;

    @PostConstruct
    public void init() {
        tokenCache = new ConcurrentSkipListMap<>();
    }

    public OAuth2AccessToken getAccessToken(String key) {
        return tokenCache.get(key);
    }

    public OAuth2AccessToken addAccessToken(String key, OAuth2AccessToken value) {
        return tokenCache.put(key, value);
    }
}
