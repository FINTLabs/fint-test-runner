package no.fint.testrunner.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BasicTestResult {
    private List<BasicTestCase> cases;
    private String message;

    public BasicTestResult() {
        cases = new ArrayList<>();
    }
}
