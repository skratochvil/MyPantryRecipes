package generator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddUser
 */
@WebServlet("/AddUser")
public class AddUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
c	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		printHtmlStart(response);
		
		Boolean validUserName = false;
		
		String userName = request.getParameter("userName");
	    String password = request.getParameter("password");
	    
	    if (userName != null) {
	    	validUserName = validateUserName(userName, password);
	    }

	    if (validUserName) {
	    	addCredentials(userName, password, response);
	    	RequestDispatcher rd = getServletContext().getRequestDispatcher("/CreateIngredientTable");
	    	request.setAttribute("newUser", userName);
			rd.forward(request, response);
	    } 
	    
	    if (!validUserName && userName != null && password != null) {	    		    			
	    			response.getWriter().append(
							"<br>" +
							"<h5 class=\"text-center text-danger\">Invalid password or username. Please try again.</h5>");	    			    		
	    }
	    
	    printHtmlEnd(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private void addCredentials(String userIn, String passwordIn, HttpServletResponse response) throws IOException {
		printHtmlStart(response);
		printHtmlEnd(response);
		
		Connection connection = null;
	     
		String insertSql = "INSERT INTO users VALUES (?, (AES_ENCRYPT(?,'passw')));";
		 
	    try {
	         DBConnection.getDBConnection();
	         connection = DBConnection.connection;
	         PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
	         preparedStmt.setString(1, userIn);
	         preparedStmt.setString(2, passwordIn);
	         preparedStmt.executeUpdate();
	      
	         connection.close(); 
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	      response.getWriter().append("Credentials added!");
	}
	
	private static void printHtmlStart(HttpServletResponse response) throws IOException {
		String start = "<!DOCTYPE html>" +
		"<html lang=\"en\">" +
		"<head>" +
		"<meta charset=\"utf-8\">" +
		"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
		"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
		"<title>Log in to My Pantry</title>" +
		"<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">" +
		"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>" +
		"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> " +
		"<style type=\"text/css\">" +
		"	.login-form {" +
		"		width: 340px;" +
		"    	margin: 50px auto;" +
		"	}" +
		"    #theForm {" +
		"    	margin-bottom: 15px;" +
		"        background: #f7f7f7;" +
		"        box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);" +
		"        padding: 30px;" +
		"    }" +
		"    .login-form h2 {" +
		"        margin: 0 0 15px;" +
		"    }" +
		"    .form-control, .btn {" +
		"        min-height: 38px;" +
		"        border-radius: 2px;" +
		"    }" +
		"    .btn {        " +
		"        font-size: 15px;" +
		"        font-weight: bold;" +
		"    }" +
		"</style>" +
		"</head>" +
		"<body>" +
		"<div class=\"login-form\">" +
		"<h1 class=\"text-primary text-center\">MyPantry Recipes</h1>" +
		"    <form id=\"theForm\" action=\"AddUser\" method=\"post\">" +
		"        <h2 class=\"text-center\">Please sign up below.</h2>       " +
		"        <div class=\"form-group\">" +
		"            <input name=\"userName\" type=\"text\" class=\"form-control\" placeholder=\"Username\" required=\"required\">" +
		"        </div>" +
		"        <div class=\"form-group\">" +
		"            <input name=\"password\" type=\"password\" class=\"form-control\" placeholder=\"Password\" required=\"required\">" +
		"        </div>" +
		"        <div class=\"form-group\">" +
		"            <button type=\"submit\" class=\"btn btn-primary btn-block\">Create Account</button>"; 

		response.getWriter().append(start);
	}
	
	private static void printHtmlEnd(HttpServletResponse response) throws IOException {
	
	String end = 
	"        </div> " +
	"    </form>" +
	" <form class=\"text-center\" action=\"Login\" method=\"POST\">		" +
	"								<button class=\"btn btn-link name=\"someLucky\" class=\"btn btn-primary\"" +
	"									type=\"submit\">Back to Login</button>				" +
	"				</form>" + 
	"</div>" +
	"</body>" +
	"</html>        ";
	
	response.getWriter().append(end);
	}
	
	public static Boolean validateUserName(String newUserName, String newPassword) {
		Connection connection = null;
	     
		 String insertSql = "SELECT * FROM users";
		  Boolean validUserName = true;
	      System.out.println(insertSql);
	      try {
	         DBConnection.getDBConnection();
	         connection = DBConnection.connection;
	         PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
	         ResultSet rs = preparedStmt.executeQuery(); 
	       
	         while (rs.next()) {
	        	 String user = rs.getString("user");
	        	 if (newUserName.equals(user) || newUserName.equals("")) {
	        		 validUserName = false;
	        	 }	       
		     } 
	         connection.close(); 
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return validUserName;
	}
}
