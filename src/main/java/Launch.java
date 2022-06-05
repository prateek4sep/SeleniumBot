import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.util.Map;

public class Launch {
    public static String launchAndGetAddress() {
        // set the driver path- You can also use WebDriverManager for drivers
        System.setProperty("webdriver.chrome.driver","Drivers/chromedriver");

        // Create object of ChromeDriver class
        ChromeDriver driver=new ChromeDriver();

        // getCapabilities will return all browser capabilities
        Capabilities cap=driver.getCapabilities();

        // asMap method will return all capability in MAP
        Map<String, Object> myCap=cap.asMap();

        // print the map data-
        System.out.println(myCap);
        String s = myCap.get("goog:chromeOptions").toString();
        s = s.substring(s.indexOf("=") + 1);
        s = s.substring(0, s.indexOf("}"));

        System.out.println(s);
        return s;
    }

    public static void launchBrowser() {
        String addr = launchAndGetAddress();
        System.out.println(addr);
        writeDebuggerAddress(addr);
    }

    public static void writeDebuggerAddress(String address){
        try{
            FileWriter fw=new FileWriter("./debug.txt");
            fw.write(address);
            fw.close();
        }catch(Exception e){System.out.println(e);}
        System.out.println("Success...");
    }
}
