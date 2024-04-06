package hk.ust.comp4321;

import java.io.File;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;

import hk.ust.comp4321.indexer.StopStem;

import javax.servlet.FilterConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import hk.ust.comp4321.extractors.PageSizeExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.IndexerPhases;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;
import hk.ust.comp4321.utils.WeightDataStorage;

import java.util.List;
import java.util.Map;

import static hk.ust.comp4321.utils.PageRank1.*;

import javax.servlet.ServletContext;
import java.io.InputStream;



@WebServlet("/add")
public class SearchEngine extends HttpServlet
{
	private static StopStem stopStem;
	private static Crawler crawler1;
	private static IndexTable db1;
	private static ForwardInvertedIndex db2;
	private static ForwardInvertedIndex db3;
	private static WeightDataStorage db4;
	private static Indexer indexer;
	private static IndexerPhases indexerPhases;
	
	public static ArrayList<String> processInput(String input, String stoppath) throws Exception {
	    // Process the input and return a vector
	    
	    // Create a new Vector to store the result
		ArrayList<String> result1 = new ArrayList<>();
	    
	    // Perform processing on the input string
	    String processedString = "Processed: " + input;
	    // Add the processed string to the result vector
	    
        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";

		//stopStem = new StopStem("resources/stopwords.txt");
		stopStem = new StopStem(stoppath);
		result1.add(stoppath);
		try {
			db1 = new IndexTable("EmCrawlerDatabase");
			db2 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabase");
//			db3 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabasePhases");
//			db4 = new WeightDataStorage("EmWeights");
			indexer = new Indexer(db1, db2, stoppath);
//			indexerPhases = new IndexerPhases(db1, db2, db3);
			crawler1 = new Crawler(db1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		int numPages = 30;
		List<String> result2 = crawler1.extractLinks(rootURL, numPages);

		if(result2.size()>1) // if size == 1, res only contain rootURL which has not been updated
		{
			for (String currentUrl : result2) {
				System.out.println(currentUrl);
				indexer.index(currentUrl);
				//indexerPhases.indexPhases(currentUrl);
			}
		}

		result1.add(processedString);
		result1.add("finished8");

		for (int rankpage = 0; rankpage < 10; rankpage++)
		{
			WebNode currentWebNode = db1.getEntry(TreeNames.id2WebNode.toString(), rankpage, WebNode.class);
			result1.add(TitleExtractor.extractTitle(currentWebNode.getUrl()));
			result1.add(currentWebNode.getUrl());

//			for (String parent : currentWebNode.getParent()) {
//				result1.add("parent " + parent);
//			}
//			for (String child : currentWebNode.getChildren()) {
//				result1.add("child " + child);
//			}
//			for (String parent : currentWebNode.getParentForRanking()) {
//				result1.add("parent for ranking " + parent);
//			}
//			for (String child : currentWebNode.getChildForRanking()) {
//				result1.add("child for ranking " + child);
//			}
			result1.add("");
		}

	    // Return the result vector
	    return result1;
	}



//	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException
//	{
//		String i = req.getParameter("num1");
//		String k = i;
//		 PrintWriter out = res.getWriter();
//		 out.println("result is " + k);
//	}
}
