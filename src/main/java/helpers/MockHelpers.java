package helpers;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gherkin.formatter.model.DataTableRow;
import cucumber.api.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static helpers.LogHelpers.logger;
import static helpers.LogHelpers.mockLogger;
import static helpers.LogHelpers.responseLogger;
import static helpers.PropertyHelpers.changeWithSeparator;
import static helpers.PropertyHelpers.getProperties;
import static io.restassured.RestAssured.given;


import java.io.File;
import java.util.Iterator;



public class MockHelpers {
    private static RestAssured restAssured;
    private static Response response;

    static String port = getProperties("wiremock.standalone.port");
    public static String mockAdress = System.getProperty("file.dir")+ changeWithSeparator("wiremock.standalone.adress");

    public static JsonObject json = null;


    public static void startWiremockStandalone(){

        ProcessBuilder processBuilder = new ProcessBuilder();

        if(System.getProperty("os.name").toLowerCase().contains("win")){
            processBuilder.command("java","-cp", "wiremock-body-transformer-1.1.6.jar;wiremock-standalone-2.27.2.jar", "com.github.tomakehurst.wiremock.standalone.WireMockServerRunner","--verbose","--extensions", "com.opentable.extension.BodyTransformer","--port", port);

        } else {
            processBuilder.command("java","-cp", "wiremock-body-transformer-1.1.6.jar:wiremock-standalone-2.27.2.jar", "com.github.tomakehurst.wiremock.standalone.WireMockServerRunner","--verbose","--extensions", "com.opentable.extension.BodyTransformer","--port", port);
        }


        processBuilder = processBuilder.directory(new File(mockAdress));

        try {
            Process process = processBuilder.start();
            Boolean mockStarted = isWiremockStarted();
            if(mockStarted == true) {
                logger.info((String.format("Wiremock has started on %s port",port)));
            } else {
                logger.fatal((String.format("Wiremock has started on %s port. Make sure the port is not in use!...",port)));
            }
        }

        catch (Exception e) {

            logger.fatal((String.format("Wiremock has NOT started on %s port",port)));
            System.out.println(e.getMessage());
        }



    }
    public static JsonObject createMap(){
     //   JsonObject json =ParserHelpers.jsonFileParser(mockAdress + System.getProperty("file.separator") + "mappings" +System.getProperty("file.separator") + mappingFile);

        String responseBody = json.get("response").getAsJsonObject().get("body").toString();

        json.get("response").getAsJsonObject().remove("body");

        json.get("response").getAsJsonObject().addProperty("body", responseBody);

        mockLogger(json, "map file");

        return json;
    }

    public static void configureJSON() {


        String responseBody = json.getAsJsonObject("response").getAsJsonObject().get("body").toString();
        json.getAsJsonObject("response").getAsJsonObject().addProperty("body", responseBody);

        int portNum =Integer.parseInt(port);
        restAssured.baseURI =String.format("http://localhost:%d/__admin/mappings/new", portNum);

        response = given().body(json.toString()).when().post();

        int statusCode = response.getStatusCode();
        if(statusCode == 200 | statusCode == 201) {
            logger.info(String.format("\tThe mock service has stubbed on [ %s ] port",port));
        } else {
            logger.fatal(String.format("\tThe mock service has NOT stubbed on [ %s ] port",port));
        }


    }

    public static JsonObject manageMap(String mappingFile, DataTable dataTable) {

       // JsonObject jsonObject = ParserHelpers.jsonFileParser(PropertyHelpers.getProperties("mock.file.adress") + mappingFile);

        Iterator iterator = dataTable.getGherkinRows().iterator();

        while (iterator.hasNext()) {

            JsonArray array = json.get("request").getAsJsonObject().get("bodyPatterns").getAsJsonArray();
            int paramSize = array.size();


            DataTableRow row = (DataTableRow)iterator.next();

            String objectName = (String)row.getCells().get(0);
            String objectValue = (String)row.getCells().get(1);

            // if object key has  a child element
            String parentObj = "";
            if(objectName.contains(".")){
                objectName= objectName.split(".")[1];
                parentObj = objectName.split(".")[0];
            }


             int j =-1;

             if(paramSize != 0) {
                 String xx = String.format("{\"matchesJsonPath\":\"$.%s\"}", objectName);

                 for (int i = 0; i < paramSize; i++) {
                     if (array.get(i).toString().equals(xx)) {
                         j = i;
                         break;
                     }
                 }
             }


             if(j != -1) {
                 //remove given param from bodyPattern

                 array.remove(j);
                 json.get("request").getAsJsonObject().remove("bodyPatterns");
                 json.get("request").getAsJsonObject().add("bodyPatterns", array);


             }

             //replace given object value from response.body
             JsonObject json2 = json.get("response").getAsJsonObject().get("body").getAsJsonObject();
             if(parentObj != ""){
                 json2 = json2.get(parentObj).getAsJsonObject();
             }

             else{
                 json2.addProperty(objectName,objectValue);

             }
             json.get("response").getAsJsonObject().remove("body");
             json.get("response").getAsJsonObject().add("body", json2);


       // JsonArray array2 = jsonObject.get("request").getAsJsonObject().get("bodyPatterns").getAsJsonArray();
        paramSize = array.size();

        if(paramSize == 0) {
            json.get("request").getAsJsonObject().remove("bodyPatterns");
            json.get("response").getAsJsonObject().remove("transformers");
        }

      }

        mockLogger(json, "managed mock file");
        return json;
    }

    public static void shutDownWiremock(){
        int portNum =Integer.parseInt(port);
        restAssured.baseURI =String.format("http://localhost:%d/__admin/shutDown", portNum);

        response = given().when().post();
        int statusCode = response.getStatusCode();
        if(statusCode == 200) {
            logger.info("Wiremock is shut down");
        }
        else if (statusCode == 404){
            logger.fatal(String.format("Wiremock is not shut down because of wiremock is not already started on %d port", portNum));

        }

    }

    public static Boolean isWiremockStarted(){
        Boolean started = false;
        int statusCode = response.getStatusCode();
        if(statusCode == 200 ) {
            started = true;
            responseLogger(response);
        }
        return started;
    }

    public static void sendMockRequest(){

        String url = json.get("request").getAsJsonObject().get("url").toString();
        restAssured.baseURI = String.format("http://localhost:%d/%s",port,url);
        response = given().when().post();
        int statusCode = response.getStatusCode();
        if(statusCode == 201 | statusCode == 200) {
            logger.info("the mock request is sent successfully");
            responseLogger(response);


        }
        else {
            logger.fatal("the mock request is NOT  sent successfully");

        }
    }
}
