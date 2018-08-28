package no.fint.testrunner.service;

import no.fint.event.model.Event;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HealthTestValidator {


    public HealthTestCase generateStatus(ResponseEntity<Event<Health>> response) {

        HealthTestCase healthTestCase = new HealthTestCase();

        if (response.getStatusCode() == HttpStatus.OK) {
            healthTestCase.setHealthData(response.getBody().getData());
            healthTestCase.setMessage(response.getBody().getMessage());
        }
        else {
            healthTestCase.setMessage(String.format("En feil oppstod (%s %s)",response.getStatusCode().toString(), response.getStatusCode().getReasonPhrase()));
        }


        verifyAdapterStatus(healthTestCase);

        return healthTestCase;


    }

    private void verifyAdapterStatus(HealthTestCase healthTestCase) {
        boolean healthy = (healthTestCase.getHealthData().stream().filter(health -> health.getStatus().equals(HealthStatus.APPLICATION_HEALTHY.toString())).count() == 1);
        if (healthy) {
            healthTestCase.setStatus(Status.OK);
            healthTestCase.setMessage("The full component stack is up running!");
        } else {
            healthTestCase.setStatus(Status.FAILED);
        }
    }

}
