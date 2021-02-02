package stepDefinions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import helpers.MockHelpers;
import helpers.ParserHelpers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import org.testng.Assert;


import java.util.Iterator;

import static helpers.MockHelpers.*;
import static helpers.ParserHelpers.jsonFileParser;
import static helpers.LogHelpers.logger;

public class Steps {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Given("^Start standalone mock with \"(.*?)\" map$")
    public void start_Stubbing(String filename)  {
        json = jsonFileParser(String.format("mock-bases/%s", filename));
        configureJSON(json);
        stubMock(filename);

    }
    @When("^Send request as standalone with below parameters:$")
    public void send_request_as_standalone_with_below_parameters(DataTable dataTable) {

        Iterator iterator = dataTable.getGherkinRows().iterator();

        JsonObject body = new JsonObject();

        while (iterator.hasNext()) {
            DataTableRow row = (DataTableRow)iterator.next();

            String key = row.getCells().get(0);
            String value = row.getCells().get(1);

            body.addProperty(key,value);
        }
        logger.info("Json body: \n "+ gson.toJson(body) + "\n");

        sendRequestWithBody(body);



    }

    @Then("^Response should include elements:$")
    public void response_should_include_elements(DataTable dataTable) throws Throwable {

        Iterator iterator = dataTable.getGherkinRows().iterator();

        JsonPath body = response.jsonPath();

        while (iterator.hasNext()) {
            DataTableRow row = (DataTableRow)iterator.next();
            String key = row.getCells().get(0);

            try{
                body.get(key);
                logger.info(String.format("The expected ' %s ' key found on the body..", key));
            }catch (Exception e) {
                Asserst.fail(String.format("The expected ' %s ' key NOT found on the body..", key));
            }
        }

    }

    @Then("^Status code should be (//d+)$")
    public void validateStatusCode(int expected){
        int actual = response.getStatusCode();
        if(actual == expected){
            logger.info(String.format("Status code is %s as expected", expected));
        } else {
            Assert.fail(String.format("Status code is %s as expected. Actual status code is %s", expected,actual));
        }

    }

    @Then("^Response should include those informations:$")
    public void response_should_include_those_informations(DataTable dataTable)  {

        Iterator iterator = dataTable.getGherkinRows().iterator();

        JsonPath body = response.jsonPath();

        while (iterator.hasNext()) {
            DataTableRow row = (DataTableRow)iterator.next();
            String key = row.getCells().get(0);
            String value = row.getCells().get(1);

            try{
                if(body.get(key).equals(value)){
                logger.info(String.format("The expected ' %s ' key found with ' %s ' on the response body..", key, body));
            }}
            catch (Exception e) {
                logger.info(String.format("The expected ' %s ' key NOT found with ' %s ' on the response body..", key, body));
            }
        }
    }

}
