import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.Map;
import java.util.Scanner;

public class TestOpenBrowser {
    public static void runStep(String fxn) throws FileNotFoundException {

        // set the driver path- You can also use WebDriverManager for drivers\
        System.setProperty("webdriver.chrome.driver","Drivers/chromedriver");
        //final String debuggerAdd = Launch.launchAndGetAddress();

        // Create object of ChromeOptions Class
        ChromeOptions opt=new ChromeOptions();

        // pass the debuggerAddress and pass the port along with host. Since I am running test on local so using localhost
        opt.setExperimentalOption("debuggerAddress",getDebuggerAddress());

        // pass ChromeOptions object to ChromeDriver constructor
        ChromeDriver driver=new ChromeDriver(opt);

        //driver.get("https://google.com");
        //driver.findElement(By.name("q")).sendKeys("Prateek");
        String method="";
        String locator="";
        String input="";
        String action="";
        Connection con = MySqlConnection.getConnection();
        if(fxn.startsWith("type")) {
            action="type";
            fxn = fxn.replaceAll("typeIn","");
            input = BreakInput.textInput(fxn);
            fxn = fxn.substring(0,fxn.indexOf('('));
            fxn = fxn.substring(0, 1).toLowerCase() + fxn.substring(1);
            method = MySqlConnection.getElementMethodByName(con, fxn);
            locator = MySqlConnection.getElementLocatorByName(con,fxn);
        } else if(fxn.startsWith("click")) {
            action="click";
            fxn = fxn.replaceAll("click","");
            fxn = fxn.substring(0,fxn.indexOf('('));
            fxn = fxn.substring(0, 1).toLowerCase() + fxn.substring(1);
            method = MySqlConnection.getElementMethodByName(con, fxn);
            locator = MySqlConnection.getElementLocatorByName(con,fxn);
        } else if(fxn.startsWith("navigate")) {
            action="navigate";
            input = BreakInput.textInput(fxn);
        }  else if(fxn.startsWith("submit")) {
            action="submit";
            fxn = fxn.replaceAll("submit","");
            input = "";
            fxn = fxn.substring(0,fxn.indexOf('('));
            fxn = fxn.substring(0, 1).toLowerCase() + fxn.substring(1);
            method = MySqlConnection.getElementMethodByName(con, fxn);
            locator = MySqlConnection.getElementLocatorByName(con,fxn);
        }   else if(fxn.startsWith("validate")) {
            action="validate";
            fxn = fxn.replaceAll("validate","");
            input = "";
            fxn = fxn.substring(0,fxn.indexOf('('));
            fxn = fxn.substring(0, 1).toLowerCase() + fxn.substring(1);
            method = MySqlConnection.getElementMethodByName(con, fxn);
            locator = MySqlConnection.getElementLocatorByName(con,fxn);
        }
        executeCommand(driver,action,method,locator,input);
    }

    public static String getDebuggerAddress() throws FileNotFoundException {
        File file = new File("./debug.txt");
        Scanner sc = new Scanner(file);
        return (sc.nextLine());
    }

    public static void executeCommand(WebDriver driver, String action, String method, String locator, String arg){
        System.out.println("Action:"+action);
        System.out.println("Method:"+method);
        System.out.println("Locator:"+locator);
        System.out.println("Input:"+arg);

        if(action.equalsIgnoreCase("navigate")){
            driver.get(arg);
            return;
        }

        WebElement element = null;
        if(method.equalsIgnoreCase("name")){
            element = driver.findElement(By.name(locator));
        } else if(method.equalsIgnoreCase("id")) {
            element = driver.findElement(By.id(locator));
        } else if(method.equalsIgnoreCase("linkText")){
            element = driver.findElement(By.linkText(locator));
        } else if(method.equalsIgnoreCase("xpath")){
            element = driver.findElement(By.xpath(locator));
        } else if(method.equalsIgnoreCase("css")){
            element = driver.findElement(By.cssSelector(locator));
        } else if(method.equalsIgnoreCase("className")){
            element = driver.findElement(By.className(locator));
        }

        if(element == null) System.out.println("Error finding element");

        if(action.contains("type")){
            element.clear();
            element.sendKeys(arg);
        } else if(action.contains("click")){
            element.click();
        } else if(action.contains("submit")){
            element.submit();
        } else if(action.contains("validate")){
            System.out.println("Validation:"+element.getText().contains(arg));
        }
    }

}
