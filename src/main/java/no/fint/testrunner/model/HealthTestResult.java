package no.fint.testrunner.model;

import lombok.Data;
import no.fint.event.model.health.Health;

import java.util.ArrayList;
import java.util.List;

@Data
public class HealthTestResult {

    List<Health> healthStatus;

    public HealthTestResult() {
        healthStatus = new ArrayList<>();
    }
}
