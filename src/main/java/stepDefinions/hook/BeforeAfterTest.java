package stepDefinions.hook;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import helpers.MockHelpers;
import helpers.ParserHelpers;
import helpers.PropertyHelpers;

public class BeforeAfterTest {

    @Before
    public void startWithStandalone(){
        String file =   PropertyHelpers.getProperties("wiremock.standalone.adress") + System.getProperty("file.separator") + "mappings" +  System.getProperty("file.separator") ;

        MockHelpers.startWiremockStandalone();
        MockHelpers.configureJSON();
    }

    @After
    public void shutDownMock(){
        MockHelpers.shutDownWiremock();
    }

}
