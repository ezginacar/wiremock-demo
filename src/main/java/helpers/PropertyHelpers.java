package helpers;

import org.testng.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHelpers {

    public static final Properties properties = new Properties();

    public static String getProperties(String propKey) {
        String property = "";
        String propAddress =System.getProperty("user.dir") + System.getProperty("file.separator")+ "src" + System.getProperty("file.separator") + "test" + System.getProperty("file.separator")+ "resources" + System.getProperty("file.separator")+"application.properties";
        try {

                InputStream input = new FileInputStream(propAddress);
                try {
                    properties.load(input);
                    property = properties.getProperty(propKey);
                    if (property == null) {
                        Assert.fail(String.format("No value found with %s key", propKey));
                    }


                }
                catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    if (input != null) {
                        input.close();
                    }

                }

                return property;
            }
        catch (Exception ex) {
            Assert.fail("\n\tException: " + ex.getMessage());
        }

        properties.clear();

        return property;
    }

    public static  String changeWithSeparator(String propKey){
        String property = getProperties(propKey);
        property = property.replace("/", System.getProperty("file.separator"));
        return property;
    }





}
