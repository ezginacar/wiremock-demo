package testData;


import com.google.gson.JsonObject;

import static helpers.LogHelpers.logger;

public class User {

    private static int id  =0;

    public static int getId() {
        return id ++ ;
    }


    public static void assignNewID(JsonObject json){
        int i = getId();
        try{
            json.get("response").getAsJsonObject().get("body").getAsJsonObject().get("data").getAsJsonObject().addProperty("id", i);
            logger.info("New ID assigned as: "+i );
        }catch (Exception exception) {
            logger.fatal(String.format("The new ID = %d is not assigned", i));

        }

    }




}
