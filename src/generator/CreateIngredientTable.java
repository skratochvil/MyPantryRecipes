package generator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateIngredientTable
 */
@WebServlet("/CreateIngredientTable")
public class CreateIngredientTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateIngredientTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String newUser = (String) request.getAttribute("newUser");
		response.getWriter().append("Creating ingredient table. Please stand by.");
		createIngredientTable(newUser);

		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/Login");
		rd.forward(request, response);
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

	private static void createIngredientTable(String newUserName) {
		Connection connection = null;
		String insertSql = "CREATE TABLE " + newUserName + "("
				+ "ingredient varchar(255),\r\n" + "isChecked INT\r\n" + ");";
		ArrayList<String> allIngredients = Ingredients.getIngredientArray();

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			PreparedStatement preparedStmt = connection
					.prepareStatement(insertSql);
			preparedStmt.execute();

			String insertSQL = "";
			for (String ingredient : allIngredients) {
				insertSQL += "('" + ingredient + "', 0),";
			}
			insertSQL = insertSQL.substring(0, insertSQL.length() - 1);
			insertSQL = "INSERT INTO " + newUserName + " VALUES " + insertSQL;
			System.out.println(insertSQL); 
				
			PreparedStatement insertStmt = connection
						.prepareStatement(insertSQL);
			insertStmt.execute();
			connection.close();
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
