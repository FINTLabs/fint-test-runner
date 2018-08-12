package no.fint.testrunner.model;

import lombok.Data;
import no.fint.event.model.health.Health;

import java.util.ArrayList;
import java.util.List;

@Data
public class HealthTestCase {
    private Status status;
    private String message;
    private List<Health> healthData;


    public HealthTestCase() {
        healthData = new ArrayList<>();
        status = Status.NOT_RUNNED;
    }
}
