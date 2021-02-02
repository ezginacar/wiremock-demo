package testRunner;


import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;


@CucumberOptions(
        features = "src/test/resources/features",
        glue = "stepDefinitions"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
