package helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gherkin.formatter.model.DataTableRow;
import cucumber.api.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.File;
import java.util.Iterator;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;


public class MockHelpers {
    public static RestAssured restAssured;
    public static Response response;
    static String port = PropertyHelpers.getProperties("wiremock.standalone.port");
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String mockAdress = System.getProperty("file.dir")+ PropertyHelpers.changeWithSeparator("wiremock.standalone.adress");
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
                System.out.println((String.format("Wiremock has started on %s port",port)));
            } else {
                System.out.println(String.format("Wiremock has started on %s port. Make sure the port is not in use!...",port));
            }
        }

        catch (Exception e) {

            System.out.println((String.format("Wiremock has NOT started on %s port",port)));
            System.out.println(e.getMessage());
        }



    }
    public static JsonObject createMap(){
     //   JsonObject json =ParserHelpers.jsonFileParser(mockAdress + System.getProperty("file.separator") + "mappings" +System.getProperty("file.separator") + mappingFile);

        String responseBody = json.get("response").getAsJsonObject().get("body").toString();

        json.get("response").getAsJsonObject().remove("body");

        json.get("response").getAsJsonObject().addProperty("body", responseBody);

        System.out.println(">>The map is: ");
        System.out.println(">>>>\n"+gson.toJson(json)+">>>>\n");

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
            System.out.println(String.format("\tThe mock service has stubbed on [ %s ] port",port));
        } else {
            System.out.println(String.format("\tThe mock service has NOT stubbed on [ %s ] port",port));
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

        System.out.println(">>The managed map is: ");
        System.out.println(">>>>\n"+gson.toJson(json)+">>>>\n");



      }
        return json;
    }

    public static void shutDownWiremock(){
        int portNum =Integer.parseInt(port);
        restAssured.baseURI =String.format("http://localhost:%d/__admin/shutDown", portNum);

        response = given().when().post();
        int statusCode = response.getStatusCode();
        if(statusCode == 200) {
            System.out.println("Wiremock is shut down");
        }
        else if (statusCode == 404){
            System.out.println(String.format("Wiremock is not shut down because of wiremock is not already started on %d port", portNum));

        }

    }

    public static Boolean isWiremockStarted(){
        Boolean started = false;
        int statusCode = response.getStatusCode();
        if(statusCode == 200 ) {
            started = true;
        }
        return started;
    }

    public static void sendMockRequest(){
        String url = json.get("request").getAsJsonObject().get("url").toString();
        baseURI = String.format("http://localhost:%d/%s",port,url);
        response = given().when().post();
        int statusCode = response.getStatusCode();
        if(statusCode == 201 | statusCode == 200) {
            System.out.println("the mock request is sent successfully");
        }
        else {
            System.out.println("the mock request is NOT  sent successfully");

        }
    }
}
