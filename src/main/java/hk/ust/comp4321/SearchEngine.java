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
	
	public static ArrayList<Double> processInput(String input, String phaseLengths, String checkboxValue, String stoppath) throws Exception {

		if (phaseLengths == null) {
			System.out.println("error: phaseLength is null, set 2 as default");
			phaseLengths = "2";
		}
		Integer phaseLength = Integer.parseInt(phaseLengths);
		String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";

		//stopStem = new StopStem("resources/stopwords.txt");
		stopStem = new StopStem(stoppath);
		System.out.println("VERSION01");
		try {
			db1 = new IndexTable("EmCrawlerDatabase");
			db2 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabase");
			db3 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabasePhases");
			db4 = new WeightDataStorage("EmWeights");
			indexer = new Indexer(db1, db2, stoppath);
			indexerPhases = new IndexerPhases(db1, db2, db3, stoppath);
			crawler1 = new Crawler(db1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("input is " + input);
		System.out.println("option is " + phaseLength);
		System.out.println("");

		int numPages = 30;
		List<String> result2 = crawler1.extractLinks(rootURL, numPages);

		boolean pageUpdated = false;
		Integer previousPhaseLength = db4.getLenEntry("phaseLength");
		if (previousPhaseLength == null)
			previousPhaseLength = 0;
		if (result2.size() > 1) // if size == 1, res only contain rootURL which has not been updated
		{
			pageUpdated = true;
			for (String currentUrl : result2) {
				System.out.println(currentUrl);
				indexer.index(currentUrl);
				if (previousPhaseLength == phaseLength)
					indexerPhases.indexPhases(currentUrl, phaseLength);
				PageRankByLink(db1, 0.5);
				PageRankByLink(db1, 0.5);
			}
		}

		if (previousPhaseLength != phaseLength) {
			for (int i = 0; i < db1.getPageId(); i++) {
				WebNode currentWebNode = db1.getEntry(TreeNames.id2WebNode.toString(), i, WebNode.class);
				indexerPhases.indexPhases(currentWebNode.getUrl(), phaseLength);
			}
		}
		if (pageUpdated == true || previousPhaseLength != phaseLength) {
			List<List<Double>> weighttp = RankStem(db1, db3, 1);
			List<List<Double>> weightbp = RankStem(db1, db3, 2);
			List<List<Double>> weightt = RankStem(db1, db2, 1);
			List<List<Double>> weightb = RankStem(db1, db2, 2);

			db4.updateEntry("weighttp", weighttp);
			db4.updateEntry("weightbp", weightbp);
			db4.updateEntry("weightt", weightt);
			db4.updateEntry("weightb", weightb);
			db4.updateLenEntry("phaseLength", phaseLength);
		}


		System.out.println("link weights are: " + getLinkWeights(db1));

		if (input == null) {
			System.out.println("error: input in search engine is null so we will not give you any webpage");
			return new ArrayList<>();
		}

		int checked = 1;
		if (checkboxValue == null)
			checked = 0;


		List<Double> scoret = RankStemWithQuery(db1, db2, input, 1, db4.getEntry("weightt"), 0, 0, stoppath);
		List<Double> scoreb = RankStemWithQuery(db1, db2, input, 2, db4.getEntry("weightb"), 0, 0, stoppath);
		List<Double> scoretp = RankStemWithQuery(db1, db3, input, 1, db4.getEntry("weighttp"), 1, phaseLength, stoppath);
		List<Double> scorebp = RankStemWithQuery(db1, db3, input, 2, db4.getEntry("weightbp"), 1, phaseLength, stoppath);

		ArrayList<Double> pageScore = PageScoreByBoth(db1, checked, scoret, scoreb, scoretp, scorebp, 5.0, 3.0, 5.0, 3.0);

		System.out.println("pageScore calculated " + pageScore);

		return pageScore;
	}

	public static ArrayList<Integer> pageRanking(ArrayList<Double> pageScore) throws Exception {
		System.out.println("pageScore for ranking " + pageScore);
		ArrayList<Integer> resultRanking = PageRankByBoth(pageScore);
		return resultRanking;
	}
	public static ArrayList<WebNode> nodeRanking(ArrayList<Integer> resultRanking) throws Exception {
		ArrayList<WebNode> resultRankedNodes = new ArrayList<>();
		for (Integer rankpage : resultRanking)
		{
			WebNode currentWebNode = db1.getEntry(TreeNames.id2WebNode.toString(), rankpage, WebNode.class);
			resultRankedNodes.add(currentWebNode);
		}
		return resultRankedNodes;
	}

	public static String nodeKeyWord(WebNode currentW) throws IOException {
		Map<String, Integer> keyword2Freq;
		keyword2Freq = db2.getKeywordFrequency(currentW.getId(), 20, 0);
		StringBuilder sb = new StringBuilder();
		keyword2Freq.forEach((k, v) -> sb.append(k).append(" ").append(v).append("; "));
		return sb.toString();
	}

//	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException
//	{
//		String i = req.getParameter("num1");
//		String k = i;
//		 PrintWriter out = res.getWriter();
//		 out.println("result is " + k);
//	}
}
