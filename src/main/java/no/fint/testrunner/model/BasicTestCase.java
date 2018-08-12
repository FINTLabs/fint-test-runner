package no.fint.testrunner.model;

import lombok.Data;

@Data
public class BasicTestCase {
    private long lastUpdated;
    private long size;
    private String resource;
    private Status status;
    private String message;


    public BasicTestCase() {
        status = Status.NOT_RUNNED;
        lastUpdated = 0;
        size = 0;
    }
}
