package stepDefinions.hook;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import helpers.MockHelpers;
import helpers.ParserHelpers;
import helpers.PropertyHelpers;

public class BeforeAfterTest {

    @Before
    public void startWithStandalone(){

        MockHelpers.startWiremockStandalone();

    }

    @After
    public void shutDownMock(){
        MockHelpers.shutDownWiremock();
    }

}
