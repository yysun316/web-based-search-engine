package hk.ust.comp4321;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;

import hk.ust.comp4321.indexer.StopStem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/add")
public class SearchEngine extends HttpServlet
{
	private static StopStem stopStem;
	
	public static ArrayList<String> processInput(String input) throws Exception {
	    // Process the input and return a vector
	    
	    // Create a new Vector to store the result
		ArrayList<String> result = new ArrayList<>();
	    
	    // Perform processing on the input string
	    String processedString = "Processed: " + input;
	    // Add the processed string to the result vector
	    
        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        
//        stopStem = new StopStem("resources/stopwords.txt");
	    
	    result.add(processedString);
	    result.add("finished");
	    
	    // Return the result vector
	    return result;
	}



//	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException
//	{
//		String i = req.getParameter("num1");
//		String k = i;
//		 PrintWriter out = res.getWriter();
//		 out.println("result is " + k);
//	}
}
