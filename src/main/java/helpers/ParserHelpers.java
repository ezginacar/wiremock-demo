package helpers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ParserHelpers {

    public static JsonObject jsonFileParser(String fileName) {
        String address = System.getProperty("user.dir") + System.getProperty("file.separator") + "src" + System.getProperty("file.separator") + "test" + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + fileName + ".json";
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = null;

        try {
            FileReader fileReader = new FileReader(address);
            jsonElement = jsonParser.parse(fileReader);
        } catch (FileNotFoundException ex) {
            System.out.println("An error occured because: " + ex.getMessage());
        }
        JsonObject jsonObject = (JsonObject) jsonElement;
        assert jsonObject != null;
        return jsonObject;


    }
}