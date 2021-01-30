package helpers;

import com.google.gson.JsonObject;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class LogHelpers {

   public static final Logger logger = Logger.getLogger(LogHelpers.class);


    public static void mockLogger(JsonObject json, String mock){
        logger.info(String.format("\n>>>>The %s is: \n  %s <<<<<<\n", mock, json));
    }

    public static void responseLogger(Response response ){
        logger.info(String.format("\n>>>>The response body is: \n  %s <<<<<<\n",response.body().prettyPrint()));
    }


}
