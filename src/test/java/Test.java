import helpers.PropertyHelpers;

public class Test {
    public static void main(String[] args) {
        String test = PropertyHelpers.getProperties("port");
        System.out.println(test);
    }

    @org.testng.annotations.Test
    public void cc(){
        String test = PropertyHelpers.getProperties("port");
        System.out.println(test);
    }
}
