package generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		printHtmlStart(response);

		String userName = request.getParameter("userName");
		String password = request.getParameter("password");

		if (userName != null && password != null) {
			Boolean valid = validateCredentials(userName, password);

			if (valid) {

				ServletContext application = getServletContext();
				application.setAttribute("currentUser", userName);

				RequestDispatcher rd = getServletContext()
						.getRequestDispatcher("/MyPantry");
				rd.forward(request, response);
			} else {
				response.getWriter().append(
						"<br>" +
						"<h5 class=\"text-center text-danger\">Invalid credentials. Please try again.</h5>");
			}
		}

		printHtmlEnd(response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public static Boolean validateCredentials(String usernameIn,
			String passwordIn) {
		Connection connection = null;

		String insertSql = "select user, AES_DECRYPT(password, 'passw') as password from users;";

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			PreparedStatement preparedStmt = connection
					.prepareStatement(insertSql);
			ResultSet rs = preparedStmt.executeQuery();

			while (rs.next()) {
				String user = rs.getString("user");
				String password = rs.getString("password");
				if (usernameIn.equals(user)) {
					if (passwordIn.equals(password)) {
						connection.close();
						return true;
					}
				}
			}
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static void printHtmlStart(HttpServletResponse response)
			throws IOException {

		String html = "<!DOCTYPE html>"
				+ "<html lang=\"en\">"
				+ "<head>"
				+ "<meta charset=\"utf-8\">"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<title>Log in to My Pantry</title>"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>"
				+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> "
				+ "<style type=\"text/css\">"
				+ "	.login-form {"
				+ "		width: 340px;"
				+ "    	margin: 50px auto;"
				+ "	}"
				+ "    #theForm {"
				+ "    	margin-bottom: 15px;"
				+ "        background: #f7f7f7;"
				+ "        box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);"
				+ "        padding: 30px;"
				+ "    }"
				+ "    .login-form h2 {"
				+ "        margin: 0 0 15px;"
				+ "    }"
				+ "    .form-control, .btn {"
				+ "        min-height: 38px;"
				+ "        border-radius: 2px;"
				+ "    }"
				+ "    .btn {        "
				+ "        font-size: 15px;"
				+ "        font-weight: bold;"
				+ "    }"
				+ "</style>"
				+ "</head>"
				+ "<body>"
				+ "<div class=\"login-form\">"
				+ "<h1 class=\"text-primary text-center\">MyPantry Recipes</h1>"
				+ "    <form id=\"theForm\" action=\"Login\" method=\"post\">"
				+ "        <h2 class=\"text-center\">Please log in below.</h2>       "
				+ "        <div class=\"form-group\">"
				+ "            <input type=\"text\" name=\"userName\" class=\"form-control\" placeholder=\"Username\" required=\"required\">"
				+ "        </div>"
				+ "        <div class=\"form-group\">"
				+ "            <input type=\"password\" name=\"password\" class=\"form-control\" placeholder=\"Password\" required=\"required\">"
				+ "        </div>"
				+ "        <div class=\"form-group\">"
				+ "            <button type=\"submit\" class=\"btn btn-primary btn-block\">Log in</button>";
				
		response.getWriter().append(html);
	}

	private static void printHtmlEnd(HttpServletResponse response) throws IOException {
		String end = "  </div> "
				+ "    </form>" +
				"<form class=\"text-center\" action=\"AddUser\" method=\"POST\">		" +
				"								<button class=\"btn btn-link name=\"redirectRegister\" class=\"btn btn-primary\"" +
				"									type=\"submit\">Create an Account</button>				" +
				"				</form>"
				
				+ "</div>" + "</body>" + "</html>    ";

		response.getWriter().append(end);
	}
}
