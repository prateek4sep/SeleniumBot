import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenerateCode {
    public static void generateCode(String path) throws SQLException {
        if(path==null || path.equals(""))
            path = "/Users/prateekmehta/InstagramLogin";
        String driverPath = path + "/Drivers/";
        String projectPath = path + "/src/main/java/";
        HashMap<Integer,String> pageMap = new HashMap<Integer, String>();
        final Connection conn = MySqlConnection.getConnection();
        ResultSet pagers = MySqlConnection.getPages(conn);
        while(pagers.next()){
            int pageId = pagers.getInt("pageId");
            String pageName = pagers.getString("pageName");
            pageMap.put(pageId,pageName);
            ResultSet elers = MySqlConnection.getElementsByPage(conn,pageId);
            ResultSet funrs = MySqlConnection.getFunctionsByPage(conn,pageId);
            write(projectPath,pageName,generatePage(pageName, elers,funrs),"java");
        }
        write(projectPath,"Base",generateBase(),"java");
        write(projectPath,"TestCases",generateTest(pageMap, conn),"java");
        write(path,"pom",generatePOM(),"xml");
    }

    public static void write(String projectPath,String name, String text, String type) {
        try {
            String path = null;
            if(type.equals("java"))
                path = projectPath+name+".java";
            else if(type.equals("xml"))
                path = projectPath+name+".xml;";
            File myObj = new File(path);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter myWriter = new FileWriter(path,false);
            myWriter.write(text);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String generateBase(){
        String text = "import org.openqa.selenium.chrome.ChromeDriver;\n" +
                "\n" +
                "import java.util.concurrent.TimeUnit;\n" +
                "\n" +
                "public class Base {\n" +
                "    protected static ChromeDriver driver;\n" +
                "\n" +
                "    public static ChromeDriver getDriver() {\n" +
                "        System.setProperty(\"webdriver.chrome.driver\",\"Drivers/chromedriver\");\n" +
                "        driver = new ChromeDriver();\n" +
                "        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);\n" +
                "        return driver;\n" +
                "    }\n" +
                "\n" +
                "    public Base(ChromeDriver driver){\n" +
                "        this.driver = driver;\n" +
                "    }\n" +
                "\n" +
                "    public static void navigateTo(String url){\n" +
                "        driver.get(url);\n" +
                "    }\n" +
                "\n" +
                "    public static void quit(){\n" +
                "        driver.quit();\n" +
                "    }\n" +
                "\n" +
                "    public static void sleep(int seconds) {\n" +
                "        try{\n" +
                "            Thread.sleep(seconds*1000);\n" +
                "        } catch (Exception e){}\n" +
                "    }\n" +
                "\n" +
                "}";
        return text;
    }

    public static String generateTest(HashMap<Integer,String> pageMap, Connection conn) throws SQLException {

        String text = "import org.openqa.selenium.chrome.ChromeDriver;\n" +
                "import org.testng.annotations.AfterClass;\n" +
                "import org.testng.annotations.Test;\n" +
                "\n" +
                "public class TestCases {\n" +
                "\n" +
                "    ChromeDriver driver = Base.getDriver();\n"+
                "    Base base = new Base(driver);\n";

        Iterator entries = pageMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            String page = thisEntry.getValue().toString();
            text += "    " + page + " " + firstSmall(page) + " = new " + page + "(driver);\n";
        }

        text += "\n";

        ResultSet testlist = MySqlConnection.getTestList(conn);
        while(testlist.next())
        {
            String test = testlist.getString("testName");
            ResultSet resultSetTests = MySqlConnection.getTestSteps(conn,test);
            test = test.replaceAll(" ","");
            test = firstSmall(test);

            text += "    @Test\n" +
                    "    public void "+test+"() {\n";

            while(resultSetTests.next()){
                String function = resultSetTests.getString("function");
                String functionName = function.substring(0,function.indexOf("("));
                System.out.println("F:"+functionName);

                if(functionName.equals("navigateTo")){
                    text += "        base."+function+";\n";
                } else {
                    int pageId = MySqlConnection.getPageIdByFunctionName(conn, functionName);
                    System.out.println("P"+pageId);
                    String page = firstSmall(pageMap.get(pageId));
                    text += "        "+page+"."+function+";\n";
                }
            }

            text += "    }\n\n";
        }

        text +="    @AfterClass\n" +
                "    public void destroy() {\n" +
                "        Base.quit();\n" +
                "    }\n}";
        return text;
    }

    public static String generatePage(String pageName, ResultSet resultSetElements, ResultSet resultSetFunctions) throws SQLException {
        HashMap<Integer,String> elemhm = new HashMap<Integer, String>();
        String text = "import org.openqa.selenium.Keys;\n" +
                "import org.openqa.selenium.WebElement;\n" +
                "import org.openqa.selenium.chrome.ChromeDriver;\n" +
                "import org.openqa.selenium.support.FindBy;\n" +
                "import org.openqa.selenium.support.PageFactory;\n" +
                "import org.testng.Assert;\n" +
                "\n" +
                "public class "+pageName+" extends Base{\n" +
                "\n" +
                "    public "+pageName+"(ChromeDriver driver) {\n" +
                "        super(driver);\n" +
                "        PageFactory.initElements(driver, this);\n" +
                "    }\n";

        while(resultSetElements.next()){
            String method  = resultSetElements.getString("method");
            String locator  = resultSetElements.getString("locator").replaceAll("\"","'");
            String elementName  = resultSetElements.getString("elementName");
            int elementId = resultSetElements.getInt("elementId");
            elemhm.put(elementId,elementName);
            text += "\n" +
                    "    @FindBy("+method+"=\""+locator+"\")\n" +
                    "    private WebElement "+elementName+";\n";
        }

        while(resultSetFunctions.next()){
            String function  = resultSetFunctions.getString("functionName");
            int eid = resultSetFunctions.getInt("elementId");
            String element = elemhm.get(eid);
            if(function.startsWith("typeIn")){
                text += "\n" +
                        "    public void "+function+"(String text){\n" +
                        "        "+element+".sendKeys(text);\n" +
                        "        "+"Base.sleep(2);\n" +
                        "    }\n";
            } else if(function.startsWith("submit")){
                text += "\n" +
                        "    public void "+function+"(){\n" +
                        "        "+element+".sendKeys(Keys.ENTER);\n" +
                        "        "+"Base.sleep(2);\n" +
                        "    }\n";
            } else if(function.startsWith("validate")){
                text += "\n" +
                        "    public void "+function+"(String text){\n" +
                        "         Assert.assertTrue("+element+".getText().contains(text));\n" +
                        "         "+"Base.sleep(2);\n" +
                        "    }\n";
            } else if(function.startsWith("click")){
                text += "\n" +
                        "    public void "+function+"(){\n" +
                        "        "+element+".click();\n" +
                        "        "+"Base.sleep(2);\n" +
                        "    }\n";
            }
        }
        text += "}";

        return text;
    }

    public static String firstSmall(String str){
        str = str.substring(0, 1).toLowerCase() + str.substring(1);
        return str;
    }

    public static String generatePOM(){
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4" +
                ".0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>org.example</groupId>\n" +
                "    <artifactId>Google</artifactId>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "\n" +
                "    <dependencies>\n" +
                "    <!-- https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-chrome-driver -->\n" +
                "    <dependency>\n" +
                "        <groupId>org.seleniumhq.selenium</groupId>\n" +
                "        <artifactId>selenium-chrome-driver</artifactId>\n" +
                "        <version>4.1.2</version>\n" +
                "    </dependency>\n" +
                "    <dependency>\n" +
                "        <groupId>org.seleniumhq.selenium</groupId>\n" +
                "        <artifactId>selenium-support</artifactId>\n" +
                "        <version>3.141.59</version>\n" +
                "        <scope>test</scope>\n" +
                "    </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.seleniumhq.selenium</groupId>\n" +
                "            <artifactId>selenium-support</artifactId>\n" +
                "            <version>3.141.59</version>\n" +
                "            <scope>compile</scope>\n" +
                "        </dependency>\n" +
                "        <!-- https://mvnrepository.com/artifact/org.testng/testng -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.testng</groupId>\n" +
                "            <artifactId>testng</artifactId>\n" +
                "            <version>7.6.0</version>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.testng</groupId>\n" +
                "            <artifactId>testng</artifactId>\n" +
                "            <version>7.6.0</version>\n" +
                "            <scope>compile</scope>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";
        return text;
    }

}
