package generator;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class HtmlUtil {
	protected static void printHtmlStart(HttpServletResponse response) throws IOException {
		 response.setContentType("text/html");
		 response.getWriter().append("<!DOCTYPE html>"
		 		+ "<html>"
		 		+ "<head>"
		 		+ "<meta charset=\"UTF-8\">"
		 		+ "<title>My Pantry</title>"
	//	 		+ "<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">"
		 		+ "<link href=\"style.css\" rel=\"stylesheet\">"
	//	 		+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>"
		 		+ "<h1>My Pantry</h1>"
		 		+ "<h5>Powered by Edamam.com</h5>"
		 		+ "</head>"
		 		+ "<body>"
		 		);
	}
		 
	protected static void printHtmlEnd(HttpServletResponse response) throws IOException {
		response.getWriter().append("</body>");
		response.getWriter().append("</html>");
	}
	
}