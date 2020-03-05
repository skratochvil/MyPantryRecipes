package generator;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
   static Connection connection = null;

   static void getDBConnection() {
      System.out.println("-------- MySQL JDBC Connection Testing ------------");
      try {
         Class.forName("com.mysql.jdbc.Driver");
      } catch (ClassNotFoundException e) {
         System.out.println("Where is your MySQL JDBC Driver?");
         e.printStackTrace();
         return;
      }
      System.out.println("MySQL JDBC Driver Registered!");
      
      connection = null;
      try {
    	  connection = DriverManager.getConnection("jdbc:mysql://ec2-3-16-194-197.us-east-2.compute.amazonaws.com/MyPantry", "sckratochvil", "Marbichons1");	  
      } catch (Exception e) {
         System.out.println("Connection Failed! Check output console");
         e.printStackTrace();
         return;
      }

      if (connection != null) {
         System.out.println("You made it, take control your database now!");
      } else {
         System.out.println("Failed to make connection!");
      }
   }

   static String getURL() {
      String url = UtilProp.getProp("url");
      System.out.println("[DBG] URL: " + url);
      return url;
   }

   static String getUserName() {
      String usr = UtilProp.getProp("user");
      System.out.println("[DBG] URL: " + usr);
      return usr;
   }

   static String getPassword() {
      String pwd = UtilProp.getProp("password");
      System.out.println("[DBG] URL: " + pwd);
      return pwd;
   }
}
