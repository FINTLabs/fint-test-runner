package no.fint.testrunner.model;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;

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

    public void generateStatus() {
        if (getLastUpdated() == -1 && getSize() == -1) {
            setStatus(Status.FAILED);
            setMessage("We had problems connecting to the endpoints.");
        } else if (getLastUpdated() == 0 && getSize() == 0) {
            setStatus(Status.FAILED);
            setMessage("Cache has never been updated.");
        } else if (getLastUpdated() > 0 && getSize() == 0) {
            setStatus(Status.PARTIALLY_FAILED);
            setMessage("Cache have been updated, but cache is empty.");
        } else if (isLastUpdatedToOld()) {
            setStatus(Status.PARTIALLY_FAILED);
            setMessage(String.format("Cache have not been updated for %s minutes", 30/*props.getLastUpdatedLimit()*/));
        } else {
            setStatus(Status.OK);
        }
    }

    private boolean isLastUpdatedToOld() {
        Instant now = Instant.now();
        Instant resourceLastUpdated = Instant.ofEpochMilli(lastUpdated);
        Duration duration = Duration.between(resourceLastUpdated, now);
        Duration limit = Duration.ofMinutes(30 /*props.getLastUpdatedLimit()*/);
        return duration.compareTo(limit) > 0;
    }
}
