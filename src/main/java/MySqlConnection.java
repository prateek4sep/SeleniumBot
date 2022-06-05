import java.sql.*;

public class MySqlConnection {
    public static Connection getConnection() {

        // creates three different Connection objects
        Connection conn = null;

        try {
            // connect way #1
            String url1 = "jdbc:mysql://localhost:3306/selbot";
            String user = "root";
            String password = "Tester@123";

            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                System.out.println("Connected to the database selbot");
            }

            //insertIntoPages(conn);
            System.out.println(getNextIdPages(conn));

        } catch (SQLException ex) {
            System.out.println("An error occurred. Maybe user/password is invalid");
            ex.printStackTrace();
        }

        return conn;
    }

    public static void insertIntoPages(Connection conn, int id, String pageName) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Inserting records into the pages table...");
            String sql = "INSERT INTO pages VALUES ("+id+", '"+pageName+"')";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromPages(Connection conn, int id) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Deleting records from the pages table...");
            String sql = "Delete FROM pages WHERE pageId="+id+"";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromElements(Connection conn, int id) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Deleting records from the elements table...");
            String sql = "Delete FROM elements WHERE elementId="+id+"";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getPages(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the pages table...");
            String sql = "Select * FROM pages ORDER BY pageId ASC";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getElements(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the elements table...");
            String sql = "Select * FROM elements ORDER BY elementId ASC";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getNextIdPages(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the pages table...");
            String sql = "Select MAX(pageId)+1 as maxId FROM pages";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("maxId");
                return id;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int getNextIdElements(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the elements table...");
            String sql = "Select MAX(elementId)+1 as maxId FROM elements";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("maxId");
                return id;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int getPageIdForPageName(Connection conn, String pageName) {
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the pages table...");
            String sql = "Select * FROM pages WHERE pageName='"+pageName+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("pageId");
                return id;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void insertIntoElements(Connection conn, int id, String elementName, String elementType, String method, String locator, int pageId) {
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Inserting records into the elements table...");
            String sql = "INSERT INTO elements VALUES ("+id+", '"+elementName+"', '"+elementType+"', '"+method+"','"+locator+"',"+pageId+")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoFunctions(Connection conn, int id, String functionName, int elementId, int pageId) {
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Inserting records into the functions table...");
            String sql = "INSERT INTO functions VALUES ("+id+", '"+functionName+"', "+elementId+","+pageId+")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getElementsList(Connection conn){
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the elements table...");
            String sql = "Select elementId, elementName FROM elements ORDER BY elementId ASC";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getNextIdFunctions(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the functions table...");
            String sql = "Select MAX(functionId)+1 as maxId FROM functions";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("maxId");
                return id;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void deleteFromFunctions(Connection conn, int id) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Deleting records from the functions table...");
            String sql = "Delete FROM functions WHERE elementId="+id+"";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNextIdTests(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the tests table...");
            String sql = "Select MAX(stepId)+1 as maxId FROM tests";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("maxId");
                return id;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void deleteFromTests(Connection conn, int id) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Deleting records from the tests table...");
            String sql = "Delete FROM tests WHERE stepId="+id+"";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoTests(Connection conn, int id, String stepDescription, String function, String testName) {
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Inserting records into the tests table...");
            String sql = "INSERT INTO tests VALUES ("+id+", '"+stepDescription+"', '"+function+"','"+testName+"')";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getTestSteps(Connection conn, String testName) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the tests table...");
            String sql = "Select * FROM tests WHERE testName='"+testName+"' ORDER BY stepId ASC";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getTestList(Connection conn) {
        // Open a connection
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the tests table...");
            String sql = "Select DISTINCT testName FROM tests ORDER BY testName ASC";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getElementMethodByName(Connection conn, String elementName){
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the tests table...");
            String sql = "Select * FROM elements WHERE elementName='"+elementName+"'";
            ResultSet rs = stmt.executeQuery(sql);
            String method = "";
            while(rs.next()){
                method = rs.getString("method");
            }
            return method;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getElementLocatorByName(Connection conn, String elementName){
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            System.out.println("Getting records from the tests table...");
            String sql = "Select * FROM elements WHERE elementName='"+elementName+"'";
            ResultSet rs = stmt.executeQuery(sql);
            String locator = "";
            while(rs.next()){
                locator = rs.getString("locator");
            }
            return locator;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ResultSet getElementsByPage(Connection conn, int pageId){
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            String sql = "Select * FROM elements WHERE pageId='"+pageId+"'";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getFunctionsByPage(Connection conn, int pageId){
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            String sql = "Select * FROM functions WHERE pageId='"+pageId+"'";
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getPageIdByFunctionName(Connection conn, String functionName){
        try{
            Statement stmt = conn.createStatement();
            // Execute a query
            String sql = "Select pageId FROM functions WHERE functionName='"+functionName+"'";
            ResultSet rs = stmt.executeQuery(sql);
            int id = -1;
            while (rs.next()){
                id = rs.getInt("pageId");
                break;
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

}