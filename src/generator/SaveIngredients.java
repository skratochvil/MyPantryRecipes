package generator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SaveIngredients
 */
@WebServlet("/SaveIngredients")
public class SaveIngredients extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveIngredients() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("saveIngredients") != null) {

			boolean atLeastOneUnchecked = false;
			for (String type : Ingredients.ingredientTypeNames) {
				if (request.getParameterValues(type) != null) {
					atLeastOneUnchecked = true;
				}
			}

			if (atLeastOneUnchecked) {
				ServletContext application = getServletContext();
				String currentUser = (String) application
						.getAttribute("currentUser");

				Connection connection = null;
				PreparedStatement preparedStatement = null;
				try {
					DBConnection.getDBConnection();
					connection = DBConnection.connection;
					String clearSql = "UPDATE " + currentUser
							+ " SET isChecked = 0";
					PreparedStatement preparedClear = connection
							.prepareStatement(clearSql);
					preparedClear.execute();

					// get values from checkboxes and build SQL string
					String sqlString = "";
					for (String type : Ingredients.ingredientTypeNames) {
						String[] checkedIngredients = request
								.getParameterValues(type);

						if (checkedIngredients != null) {
							for (String ingredient : checkedIngredients) {

								sqlString += "\"" + ingredient + "\"" + ",";
							}
						}
					}

					if (sqlString != null && sqlString.length() > 0) {
						sqlString = sqlString.substring(0,
								sqlString.length() - 1);
					}
					
					System.out.println("First string:" + sqlString);

					String updateSql = "UPDATE " + currentUser
							+ " SET isChecked = 1 " + "WHERE ingredient in ("
							+ sqlString + ");";
					
					System.out.println(updateSql);

					preparedStatement = connection.prepareStatement(updateSql);
					preparedStatement.execute();
				} catch (Exception E) {
					E.printStackTrace();
				}
			}

		}

		if (request.getParameter("loadIngredients") != null) {
			response.sendRedirect(request.getContextPath() + "/MyPantry");
			return;
		}

		if (request.getParameter("clearIngredients") != null) {
			ServletContext application = getServletContext();
			String currentUser = (String) application
					.getAttribute("currentUser");

			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try {
				DBConnection.getDBConnection();
				connection = DBConnection.connection;
				String clearSql = "UPDATE " + currentUser
						+ " SET isChecked = 0";
				PreparedStatement preparedClear = connection
						.prepareStatement(clearSql);
				preparedClear.execute();
			} catch (Exception E) {
				E.printStackTrace();
			}
		}

		if (request.getParameter("checkIngredients") != null) {
			ServletContext application = getServletContext();
			String currentUser = (String) application
					.getAttribute("currentUser");

			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try {
				DBConnection.getDBConnection();
				connection = DBConnection.connection;
				String clearSql = "UPDATE " + currentUser
						+ " SET isChecked = 1";
				PreparedStatement preparedClear = connection
						.prepareStatement(clearSql);
				preparedClear.execute();
			} catch (Exception E) {
				E.printStackTrace();
			}
		}

		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/MyPantry");
		rd.forward(request, response);
	}

}
