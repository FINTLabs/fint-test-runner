package no.fint.testrunner.service;

import no.fint.testrunner.Props;
import no.fint.testrunner.model.Status;
import no.fint.testrunner.model.BasicTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class BasicTestValidator {

    @Autowired
    Props props;

    public void generateStatus(BasicTestCase basicTestCase) {

        if (basicTestCase.getLastUpdated() == 0 && basicTestCase.getSize() == 0) {
            basicTestCase.setStatus(Status.FAILED);
            basicTestCase.setMessage("Cache has never been updated.");
            return;
        }

        if (basicTestCase.getLastUpdated() > 0 && basicTestCase.getSize() == 0) {
            basicTestCase.setStatus(Status.PARTIALLY_FAILED);
            basicTestCase.setMessage("Cache have been updated, but cache is empty.");
            return;
        }

        if (isLastUpdatedToOld(basicTestCase.getLastUpdated())) {
            basicTestCase.setStatus(Status.PARTIALLY_FAILED);
            basicTestCase.setMessage(String.format("Cache have not been updated for %s minutes", props.getLastUpdatedLimit()));
            return;
        }

        basicTestCase.setStatus(Status.OK);


    }

    private boolean isLastUpdatedToOld(long lastUpdated) {
        Instant now = Instant.now();
        Instant resourceLastUpdated = Instant.ofEpochMilli(lastUpdated);
        Duration duration = Duration.between(resourceLastUpdated, now);
        Duration limit = Duration.ofMinutes(props.getLastUpdatedLimit());
        return duration.compareTo(limit) > 0;
    }
}
