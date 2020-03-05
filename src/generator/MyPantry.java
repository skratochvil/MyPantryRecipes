package generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Servlet implementation class MyPantry
 */
@WebServlet("/MyPantry")
public class MyPantry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyPantry() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		ServletContext application = getServletContext();

		String currentUser = (String) application.getAttribute("currentUser");

		HashMap<String, Integer> ingredientMap = getIngredientsFromDatabase(currentUser);

		printHtmlStart(response, currentUser);

		printIngredients(response, ingredientMap);

		// execute method for a button if one was pressed
		if (request.getParameter("keywordSearch") != null) {
			String theQuery = request.getParameter("keywordSearch");
			keywordSearch(response, theQuery);
		}

		if (request.getParameter("haveAllIngredientSearch") != null) {
			String theQuery = request.getParameter("haveAllIngredientSearch");
			haveAllIngredientSearch(response, theQuery, ingredientMap, false);
		}

		if (request.getParameter("someLucky") != null) {			
			feelingSomeLucky(ingredientMap, response);
		}

		if (request.getParameter("allLucky") != null) {
			feelingAllLucky(ingredientMap, response);
		}

		// close HTML page
		response.getWriter().append("</body></html>");
	}
	
	private static void feelingAllLucky(HashMap<String, Integer> ingredientMap,
			HttpServletResponse response) throws IOException {
		String queryString = "";

		ArrayList<String> ownedIngredients = new ArrayList();
		ownedIngredients.add(getRandomBean(ingredientMap));	
		ownedIngredients.add(getRandomVeggie(ingredientMap));
		ownedIngredients.add(getRandomMeat(ingredientMap));
		ownedIngredients.add(getRandomSeafood(ingredientMap));
		ownedIngredients.add(getRandomFruit(ingredientMap));
		ownedIngredients.add(getRandomGrain(ingredientMap));
		ownedIngredients.add(getRandomAlcohol(ingredientMap));
		ownedIngredients.add(getRandomDairy(ingredientMap));
		Collections.shuffle(ownedIngredients);
		int count = 0;
		for (String ingredient : ownedIngredients) {
			if (ingredient != "" && count <= 1) {
				queryString += ingredient;
				count++;
			}
		}		
		System.out.println(queryString);
		haveAllIngredientSearch(response, queryString, ingredientMap, true);
	}	
		
	private static void feelingSomeLucky(HashMap<String, Integer> ingredientMap,
			HttpServletResponse response) throws IOException {
		String queryString = "";

		ArrayList<String> ownedIngredients = new ArrayList();
		ownedIngredients.add(getRandomBean(ingredientMap));	
		ownedIngredients.add(getRandomVeggie(ingredientMap));
		ownedIngredients.add(getRandomMeat(ingredientMap));
		ownedIngredients.add(getRandomSeafood(ingredientMap));
		ownedIngredients.add(getRandomFruit(ingredientMap));
		ownedIngredients.add(getRandomGrain(ingredientMap));
		ownedIngredients.add(getRandomAlcohol(ingredientMap));
		ownedIngredients.add(getRandomDairy(ingredientMap));
		Collections.shuffle(ownedIngredients);
		int count = 0;
		for (String ingredient : ownedIngredients) {
			if (ingredient != "" && count <= 1) {
				queryString += ingredient;
				count++;
			}
		}		
		System.out.println(queryString);
		keywordSearch(response, queryString);
	}	

	private static void redirect(RequestDispatcher rd,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		rd.forward(request, response);
	}

	private static void keywordSearch(HttpServletResponse response, String query)
			throws IOException {
		String firstUrl = "https://api.edamam.com/search?q=";
		String[] splitQuery = query.split(" ");

		for (String queryTerm : splitQuery) {
			firstUrl = firstUrl + queryTerm + ",";
		}

		String secondUrl = "&app_id=f9f089b4&app_key=d117380e5c25ab99a0efaf0de8deffeb&from=0&to=100";
		String finalUrl = firstUrl + secondUrl;

		URL url = new URL(finalUrl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		Gson g = new Gson();

		Response result = g.fromJson(content.toString(), Response.class);

		Hit[] hits = result.hits;
		for (Hit recipe : hits) {
			System.out.println(recipe.recipe.label);
			response.getWriter().append(
					"<div style=\"margin:auto;width:50%;padding:10px;\">");

			response.getWriter().append(
					"<a href=" + "\"" + recipe.recipe.url + "\">");
			response.getWriter().append(
					"<h4>" + recipe.recipe.label + "</h4>");
			response.getWriter().append(
					"<img src=\"" + recipe.recipe.image + "\">");
			response.getWriter().append("</a>");
			response.getWriter().append("</div>");
		}
		
		if (hits.length == 0) {	
				response.getWriter().append("<h4 class=\"text-danger\">Either your search returned no results, "
						+ "or we've exceeded our number of API calls per minute. Please wait a moment and try again, or try a new search.</h4>");
		}
	}
		
	private static ArrayList<String> getIngredientArrayList(HashMap<String, Integer> ingredientMap) {
		ArrayList<String> ownedIngredientList = new ArrayList();
		for (String key : ingredientMap.keySet()) {
			if (1 == ingredientMap.get(key)) {
				ownedIngredientList.add(key);
			}
		}
		return ownedIngredientList;
	}

	private static void haveAllIngredientSearch(HttpServletResponse response,
			String query, HashMap<String, Integer> ingredientMap, Boolean lucky)
			throws IOException {
		
		if (lucky) {
		response.getWriter().append("<h4>Searching your ingredients: " + query + "</h4>");
		}
		
		String firstUrl = "https://api.edamam.com/search?q=";
		String[] splitQuery = query.split(" ");

		for (String queryTerm : splitQuery) {
			firstUrl = firstUrl + queryTerm + ",";
		}

		String secondUrl = "&app_id=f9f089b4&app_key=d117380e5c25ab99a0efaf0de8deffeb&from=0&to=100";
		String finalUrl = firstUrl + secondUrl;

		URL url = new URL(finalUrl);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		Gson g = new Gson();

		Response result = g.fromJson(content.toString(), Response.class);
		ArrayList<String> missingIngredients = new ArrayList<String>();
		
		Hit[] hits = result.hits;
		Boolean atLeastOneToDisplay = false;	
		for (Hit recipe : hits) {
			Boolean hasAllIngredients = true;

			Ingredient[] requiredIngredients = recipe.recipe.ingredients;

			ArrayList<String> ownedIngredientList = getIngredientArrayList(ingredientMap);
			
			for (Ingredient requiredIngredient : requiredIngredients) {
				Boolean hasIngredient = false;
				for (String ownedIngredient : ownedIngredientList) {
					if (requiredIngredient.text.toLowerCase().contains(
							ownedIngredient)) {
						hasIngredient = true;
						break;
					}
				}

				if (hasIngredient == false) {
					missingIngredients.add(requiredIngredient.text);
					System.out.println("Does not have "
							+ requiredIngredient.text);
					hasAllIngredients = false;
					break;
				}
			}

			if (hasAllIngredients) {
				atLeastOneToDisplay = true;
				System.out.println(recipe.recipe.label);
				response.getWriter().append(
						"<div class=\"text-center\">");
				response.getWriter().append(
						"<a href=" + "\"" + recipe.recipe.url + "\">");
				response.getWriter().append(
						"<h2>" + recipe.recipe.label + "</h2>");
				response.getWriter().append(
						"<img src=\"" + recipe.recipe.image + "\">");
				response.getWriter().append("</a>");
				response.getWriter().append("</div>");
			}
		}	
		
		if (!atLeastOneToDisplay) {
			response.getWriter().append("<h4 class=\"text-danger\">Either your search returned no results, "
					+ "or we've exceeded our number of API calls per minute. Please wait a moment and try again, or try a new search.</h4>");
		}
		
		if(missingIngredients.size() > 0) {
			response.getWriter().append("<div class=\"panel-body\"><p></p><p class=\"text-primary\">Here are a few ingredients you could add to get more results: ");
			String missingString = "";
			for (int a = 0; a < 10 && a < missingIngredients.size(); a++) {
				missingString += missingIngredients.get(a) + ", ";
			}
			missingString = missingString.substring(0, missingString.length() - 2);
			response.getWriter().append(missingString + "</p></div>");	
		}
	}

	private static void printHtmlStart(HttpServletResponse response,
			String currentUser) throws IOException {

		String start = "<!DOCTYPE html>"
				+ "<html lang=\"en\">"
				+ "<head>"
				+ "<meta charset=\"UTF-8\">"
				+ "    <title>My Pantry</title>"
				+ "<link rel=\"stylesheet\""
				+ "	href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css\">"
				+ "<script"
				+ "	src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>"
				+ "<script"
				+ "	src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js\"></script>"
				+ "<script src=\"https://developer.edamam.com/attribution/badge.js\"></script>"
				+ "</head>"
				+ "<body>"
				+ "<h1 class=\"text-primary text-center\">MyPantry Recipes</h1>"
				+ "	<div class=\"container\">"
				+ "		<div class=\"row row-centered\">"
				+ "			<div class=\"col-xs-6 col-centered\"></div>"
				+ "			<div class=\"col-xs-6 col-centered\"></div>"
				+ "			<div class=\"col-xs-3 col-centered\"></div>"
				+ "			<div class=\"col-xs-3 col-centered\"></div>"
				+ "			<div class=\"col-xs-3 col-centered\"></div>"
				+ "			<div class=\"col-xs-3 col-centered\">"
				+ "				<div class=\"text-center pull-right\" id=\"edamam-badge\""
				+ "					data-color=\"white\"></div>"
				+ "			</div>"
				+ "		</div>"
				+ "	</div>"
				+ "	<h2 class=\"text-primary\">Find recipes by:</h2>"
				+ "	<ul class=\"nav nav-tabs\">"
				+ "		<li class=\"active\"><a data-toggle=\"tab\" href=\"#home\">Keyword</a></li>"
				+ "		<li><a data-toggle=\"tab\" href=\"#menu1\">My Ingredients</a></li>"

				+ "		<li><a data-toggle=\"tab\" href=\"#menu2\">I'm Feeling Lucky</a></li>"
				+ "	</ul>"
				+ "	<div class=\"tab-content\">"
				+ "		<div id=\"home\" class=\"tab-pane fade in active\">"
				+ "			<div class=\"container\">"
				+ "				<h3 class=\"text-primary text-center\">Keyword Search</h3>"
				+ "				<form action=\"MyPantry\" method=\"POST\">"
				+ "					<div class=\"form-group\">"
				+ "						<label for=\"\">Search by one or more of the following:"
				+ "							ingredient, dish, keyword, or cuisine type.</label> <input"
				+ "							class=\"form-control\" type=\"text\" name=\"keywordSearch\">"
				+ "					</div>"
				+ "					<div class=\"form-group text-right\">"
				+ "						<button class=\"btn btn-primary btn-lg\" type=\"submit\">"
				+ "							Search</button>"
				+ "					</div>"
				+ "				</form>"
				+ "			</div>"
				+ "		</div>"
				+ "		<div id=\"menu1\" class=\"tab-pane fade\">"
				+ "			<div class=\"container\">"
				+ "				<h3 class=\"text-primary text-center\">MyPantry Search</h3>"
				+ "				<form action=\"MyPantry\" method=\"POST\">"
				+ "					<div class=\"form-group\">"
				+ "						<label for=\"\">Enter some keywords, cuisines, or ingredients. You will only see"
				+ "							recipes that you have everything you need for.</label> <input"
				+ "							class=\"form-control\" type=\"text\""
				+ "							name=\"haveAllIngredientSearch\">"
				+ "					</div>"
				+ "					<div class=\"form-group text-right\">"
				+ "						<button class=\"btn btn-primary btn-lg\" type=\"submit\">"
				+ "							Search</button>"
				+ "					</div>"
				+ "				</form>"
				+ "			</div>"
				+ "		</div>"
				+ "     <div id=\"menu2\" class=\"tab-pane fade\">"
				+ "			<div class=\"container\">"
				+ "				<h3 class=\"text-primary text-center\">I'm Feeling Lucky</h3>"
				+ "				<form class=\"text-center\" action=\"MyPantry\" method=\"POST\">"
				+ "					<div class=\"form-group text-center\">"
				+ "						<label for=\"\">Get random recipes based on your ingredients. Try Some Ingredients if you"
				+ "						don't mind picking up a few more ingredients, or All Ingredients to only get results where you have everything you need.</label>"
				+ "					</div>"
				+ "					<div class=\"panel-body\">"
				+ "					"
				+ "					<div class=\"control-group\">"
				+ "							<div class=\"col-xs-6 text-right\">"
				+ "								<button name=\"someLucky\" class=\"btn btn-primary btn-lg\""
				+ "									type=\"submit\">Some of My Ingredients</button>"
				+ "							</div>"
				+ "							<div class=\"col-xs-6 text-left\">"
				+ "								<button name=\"allLucky\" class=\"btn btn-primary btn-lg\""
				+ "									type=\"submit\">All of My Ingredients</button>"
				+ "							</div>" + "						</div>" + "					" + "						</div>"
				+ "				</form>" + "			</div>" + "		</div>" + "</div>";

		response.getWriter().append(start);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void printIngredients(HttpServletResponse response,
			HashMap<String, Integer> ingredientMap) throws IOException {

		String vegString = "<div class=\"panel panel-default\">"
				+ "		<div class=\"panel-heading\">"
				+ "			<h4 class=\"panel-title\">"
				+ "				<a data-toggle=\"collapse\" href=\"#collapseThree\"> My Ingredients >>"
				+ "				</a>"
				+ "			</h4>"
				+ "		</div>"
				+ "		<div id=\"collapseThree\" class=\"panel-collapse collapse\">"
				+ "						<form action=\"SaveIngredients\" method=\"POST\""
				+ "							name=\"updateIngredients\">"
				+ "			<div class=\"panel-body\">"
				+ "				<div class=\"panel-group accordion\" id=\"ingredientAccordion\">"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseThreeOne\">"
				+ "									Vegetables </a>"
				+ "							</h4>"
				+ "						</div>"

				+ "							<div id=\"collapseThreeOne\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "								<div class=\"panel-body\">"
				+ "									<table class=\"table table-dark text-primary\">";
		response.getWriter().append(vegString);
		printCheckboxes(ingredientMap, response, 0);
		String dairyString = "									</table>"
				+ "								</div>"
				+ "							</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseDairy\"> Dairy </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseDairy\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "								<table class=\"table table-dark text-primary\">";
		response.getWriter().append(dairyString);
		printCheckboxes(ingredientMap, response, 1);
		String grainString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseGrains\"> Grains,"
				+ "									Pasta, & Baking </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseGrains\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "								<table class=\"table table-dark text-primary\">";

		response.getWriter().append(grainString);
		printCheckboxes(ingredientMap, response, 2);
		String meatString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseMeats\"> Meats </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseMeats\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(meatString);
		printCheckboxes(ingredientMap, response, 3);
		String seaString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseSeafood\"> Seafood </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseSeafood\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(seaString);
		printCheckboxes(ingredientMap, response, 4);
		String beanString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseLegumes\" > Beans &"
				+ "									Legumes </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseLegumes\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(beanString);
		printCheckboxes(ingredientMap, response, 5);
		String fruitString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseFruits\"> Fruits </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseFruits\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(fruitString);
		printCheckboxes(ingredientMap, response, 6);
		String herbString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseSpices\"> Herbs &"
				+ "									Spices </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseSpices\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(herbString);
		printCheckboxes(ingredientMap, response, 7);
		String alcoholString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseAlcohol\"> Alcohol </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseAlcohol\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(alcoholString);
		printCheckboxes(ingredientMap, response, 8);
		String condimentString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseCondiments\">"
				+ "									Condiments & Sauces </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseCondiments\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(condimentString);
		printCheckboxes(ingredientMap, response, 9);
		String otherString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "					<div class=\"panel panel-default\">"
				+ "						<div class=\"panel-heading\">"
				+ "							<h4 class=\"panel-title\">"
				+ "								<a data-toggle=\"collapse\" href=\"#collapseOther\"> Other </a>"
				+ "							</h4>"
				+ "						</div>"
				+ "						<div id=\"collapseOther\" class=\"panel-collapse collapse\" data-parent=\"#ingredientAccordion\">"
				+ "							<div class=\"panel-body\">"
				+ "							<table class=\"table table-dark text-primary\">";
		response.getWriter().append(otherString);
		printCheckboxes(ingredientMap, response, 10);
		String closingString = "								</table>"
				+ "							</div>"
				+ "						</div>"
				+ "					</div>"
				+ "				</div>"
				+ "			</div>"
				+ "			<div class=\"panel-body text-center\">"
				+ "				<button name=\"saveIngredients\" class=\"btn btn-primary btn-lg\""
				+ "					type=\"submit\">Save Ingredients</button>"
				+ "				<button name=\"loadIngredients\" class=\"btn btn-primary btn-lg\" type=\"submit\">Load"
				+ "					Ingredients</button>"
				+ "				<button name=\"clearIngredients\" class=\"btn btn-primary btn-lg\" type=\"submit\">Clear"
				+ "					Ingredients</button>"
				+ "				<button name=\"checkIngredients\" class=\"btn btn-primary btn-lg\" type=\"submit\">Check"
				+ "					All Ingredients</button>" + "			</div>" + "			</form>"
				+ "		</div>" + "	</div>";

		response.getWriter().append(closingString);
	}

	private static HashMap<String, Integer> getIngredientsFromDatabase(String currentUser) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String selectSQL = "select * from " + currentUser + ";";

		HashMap<String, Integer> ingredientMap = new HashMap();

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			preparedStatement = connection.prepareStatement(selectSQL);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ingredientMap.put(rs.getString("ingredient"),
						rs.getInt("isChecked"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//add some basic things and common variants
		ingredientMap.put("water", 1);
		ingredientMap.put("ice", 1);
		if(ingredientMap.get("bay leaves") == 1) {
			ingredientMap.put("bay leaf", 1);
		}
		if(ingredientMap.get("buffalo sauce") == 1) {
			ingredientMap.put("buffalo wing sauce", 1);
		}
		if(ingredientMap.get("vegetable stock") == 1) {
			ingredientMap.put("vegetable broth", 1);
		}
		if(ingredientMap.get("beef stock") == 1) {
			ingredientMap.put("beef broth", 1);
		}
		if(ingredientMap.get("chicken stock") == 1) {
			ingredientMap.put("chocken broth", 1);
		}
		if(ingredientMap.get("jalapeno") == 1) {
			ingredientMap.put("jalapeño", 1);
		}
		if(ingredientMap.get("chilies") == 1) {
			ingredientMap.put("chili", 1);
		}
		if(ingredientMap.get("chilies") == 1) {
			ingredientMap.put("chilis", 1);
		}
		if(ingredientMap.get("chilies") == 1) {
			ingredientMap.put("chillis", 1);
		}
		if(ingredientMap.get("chilies") == 1) {
			ingredientMap.put("chillies", 1);
		}
		return ingredientMap;
	}

	private static void printCheckboxes(HashMap<String, Integer> ingredientMap,
			HttpServletResponse response, int ingredientType)
			throws IOException {
		response.getWriter().append("<tr class=\"bg-primary\">");
		int counter = 0;

		for (String ingredient : Ingredients.ingredientTypes[ingredientType]) {
			if (counter % 7 == 0 && counter != 0) {
				response.getWriter().append("</tr><tr class=\"bg-primary\">");
			}

			response.getWriter().append("<td><label><input type=\"checkbox\"");

			if (ingredientMap.get(ingredient) == 1) {
				response.getWriter().append(" checked");
			}
			response.getWriter().append(
					" name=\""
							+ Ingredients.ingredientTypeNames[ingredientType]
							+ "\" value=\"" + ingredient);

			response.getWriter().append(
					"\"/>" + " " + ingredient + "</label></td>");
			counter++;
		}
	}
	
	private static String getRandomMeat(HashMap<String, Integer> ingredientMap) {
		String returnMeat = "";
		String[] possibleMeats = Ingredients.ingredientTypes[3];
		ArrayList<String> presentMeats = new ArrayList();
		for (String meat : possibleMeats) {
			if (ingredientMap.get(meat) == 1) {
				presentMeats.add(meat);
			}
		}
		if (presentMeats.size() > 0) {
			int rnd = new Random().nextInt(presentMeats.size());
			returnMeat = presentMeats.get(rnd) + " ";
		}
		return returnMeat;
	}
	
	private static String getRandomVeggie(HashMap<String, Integer> ingredientMap) {
		String returnVeggie = "";
		String[] possibleVeggies = Ingredients.ingredientTypes[0];
		ArrayList<String> presentVeggies = new ArrayList();
		for (String veggie : possibleVeggies) {
			if (ingredientMap.get(veggie) == 1) {
				presentVeggies.add(veggie);
			}
		}
		if (presentVeggies.size() > 0) {
			int rnd = new Random().nextInt(presentVeggies.size());
			returnVeggie = presentVeggies.get(rnd) + " ";
		}
		return returnVeggie;
	}
	
	private static String getRandomBean(HashMap<String, Integer> ingredientMap) {
		String returnBean = "";
		String[] possibleBeans = Ingredients.ingredientTypes[5];
		ArrayList<String> presentBeans = new ArrayList();
		for (String bean : possibleBeans) {
			if (ingredientMap.get(bean) == 1) {
				presentBeans.add(bean);
			}
		}
		if (presentBeans.size() > 0) {
			int rnd = new Random().nextInt(presentBeans.size());
			returnBean = presentBeans.get(rnd) + " ";
		}
		return returnBean;
	}
	
	private static String getRandomDairy(HashMap<String, Integer> ingredientMap) {
		String returnDairy = "";
		String[] possibleDairy = Ingredients.ingredientTypes[1];
		ArrayList<String> presentDairy = new ArrayList();
		for (String dairy : possibleDairy) {
			if (ingredientMap.get(dairy) == 1) {
				presentDairy.add(dairy);
			}
		}
		if (presentDairy.size() > 0) {
			int rnd = new Random().nextInt(presentDairy.size());
			returnDairy = presentDairy.get(rnd) + " ";
		}
		return returnDairy;
	}

	private static String getRandomSeafood(HashMap<String, Integer> ingredientMap) {
		String returnDairy = "";
		String[] possibleDairy = Ingredients.ingredientTypes[4];
		ArrayList<String> presentDairy = new ArrayList();
		for (String dairy : possibleDairy) {
			if (ingredientMap.get(dairy) == 1) {
				presentDairy.add(dairy);
			}
		}
		if (presentDairy.size() > 0) {
			int rnd = new Random().nextInt(presentDairy.size());
			returnDairy = presentDairy.get(rnd) + " ";
		}
		return returnDairy;
	}
	
	private static String getRandomGrain(HashMap<String, Integer> ingredientMap) {
		String returnDairy = "";
		String[] possibleDairy = Ingredients.ingredientTypes[2];
		ArrayList<String> presentDairy = new ArrayList();
		for (String dairy : possibleDairy) {
			if (ingredientMap.get(dairy) == 1) {
				presentDairy.add(dairy);
			}
		}
		if (presentDairy.size() > 0) {
			int rnd = new Random().nextInt(presentDairy.size());
			returnDairy = presentDairy.get(rnd) + " ";
		}
		return returnDairy;
	}
	
	private static String getRandomFruit(HashMap<String, Integer> ingredientMap) {
		String returnDairy = "";
		String[] possibleDairy = Ingredients.ingredientTypes[6];
		ArrayList<String> presentDairy = new ArrayList();
		for (String dairy : possibleDairy) {
			if (ingredientMap.get(dairy) == 1) {
				presentDairy.add(dairy);
			}
		}
		if (presentDairy.size() > 0) {
			int rnd = new Random().nextInt(presentDairy.size());
			returnDairy = presentDairy.get(rnd) + " ";
		}
		return returnDairy;
	}
	
	private static String getRandomAlcohol(HashMap<String, Integer> ingredientMap) {
		String returnDairy = "";
		String[] possibleDairy = Ingredients.ingredientTypes[8];
		ArrayList<String> presentDairy = new ArrayList();
		for (String dairy : possibleDairy) {
			if (ingredientMap.get(dairy) == 1) {
				presentDairy.add(dairy);
			}
		}
		if (presentDairy.size() > 0) {
			int rnd = new Random().nextInt(presentDairy.size());
			returnDairy = presentDairy.get(rnd) + " ";
		}
		return returnDairy;
	}

	class Response {
		@SerializedName("q")
		String q;
		@SerializedName("hits")
		Hit[] hits;
	}

	class Hit {

		@SerializedName("recipe")
		Recipe recipe;
	}

	class Recipe {
		@SerializedName("uri")
		String uri;
		@SerializedName("label")
		String label;
		@SerializedName("image")
		String image;
		@SerializedName("url")
		String url;
		@SerializedName("ingredients")
		Ingredient[] ingredients;
	}

	class Ingredient {
		@SerializedName("foodId")
		String foodId;
		@SerializedName("quantity")
		float quantity;
		@SerializedName("food")
		Food food;
		@SerializedName("text")
		String text;
	}

	class Food {
		@SerializedName("foodId")
		String foodId;
		@SerializedName("label")
		String label;
	}

}
