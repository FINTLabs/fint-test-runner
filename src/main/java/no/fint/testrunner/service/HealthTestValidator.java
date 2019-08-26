package no.fint.testrunner.service;

import no.fint.event.model.Event;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.testrunner.model.HealthTestCase;
import no.fint.testrunner.model.Status;
import org.springframework.stereotype.Service;

@Service
public class HealthTestValidator {

    public HealthTestCase generateStatus(Event<Health> healthEvent) {
        HealthTestCase healthTestCase = new HealthTestCase();
        healthTestCase.setHealthData(healthEvent.getData());
        healthTestCase.setMessage(healthEvent.getMessage());

        verifyAdapterStatus(healthTestCase);

        return healthTestCase;
    }

    private void verifyAdapterStatus(HealthTestCase healthTestCase) {
        boolean healthy = healthTestCase
                .getHealthData()
                .stream()
                .map(Health::getStatus)
                .anyMatch(HealthStatus.APPLICATION_HEALTHY.toString()::equals);

        boolean unhealthy = healthTestCase
                .getHealthData()
                .stream()
                .map(Health::getStatus)
                .anyMatch(HealthStatus.APPLICATION_UNHEALTHY.toString()::equals);

        if (healthy) {
            healthTestCase.setStatus(Status.OK);
        } else if (unhealthy) {
            healthTestCase.setStatus(Status.PARTIALLY_FAILED);
        } else {
            healthTestCase.setStatus(Status.FAILED);
        }
    }

}
