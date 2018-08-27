package no.fint.testrunner.utilities;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class HttpHeaderService {


    public HttpHeaders createHeaders(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        return headers;
    }

    public HttpHeaders createPwfHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.set("x-org-id", "pwf.no");
        headers.set("x-client", "pwf_no_client");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

        return headers;
    }


}
