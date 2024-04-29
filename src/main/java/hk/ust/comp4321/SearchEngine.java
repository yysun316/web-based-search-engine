//package hk.ust.comp4321;
//
//import hk.ust.comp4321.crawler.Crawler;
//import hk.ust.comp4321.indexer.Indexer;
//import hk.ust.comp4321.indexer.StopStem;
//import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
//import hk.ust.comp4321.invertedIndex.IndexTable;
//import hk.ust.comp4321.utils.TreeNames;
//import hk.ust.comp4321.utils.WebNode;
//import hk.ust.comp4321.utils.WeightDataStorage;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static hk.ust.comp4321.indexer.dealWithPhases.weightIncreaseByPhase;
//import static hk.ust.comp4321.utils.PageRank.*;
//
//@WebServlet("/add")
//public class SearchEngine extends HttpServlet
//{
//	private static StopStem stopStem;
//	private static Crawler crawler1;
//	private static IndexTable db1;
//	private static ForwardInvertedIndex db2;
//	private static WeightDataStorage db4;
//	private static Indexer indexer;
//
//	// the main function to be called by jsp
//	public static ArrayList<Double> processInput(String input, String checkboxValue, String stoppath) throws Exception {
//
//		String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
//
//		stopStem = new StopStem(stoppath);
//		try {
//			db1 = new IndexTable("EmCrawlerDatabase");
//			db2 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabase");
//			db4 = new WeightDataStorage("EmWeights");
//			indexer = new Indexer(db1, db2, stoppath);
//			crawler1 = new Crawler(db1);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		int numPages = 300;
//		List<String> result2 = crawler1.extractLinks(rootURL, numPages);
//
//		boolean pageUpdated = false;
//		if (!result2.isEmpty()) // if size == 1, res only contain rootURL which has not been updated
//		{
//			pageUpdated = true;
//			for (String currentUrl : result2) {
//				System.out.println(currentUrl);
//				indexer.index(currentUrl);
//				PageRankByLink(db1, 0.5);
//				PageRankByLink(db1, 0.5);
//			}
//			PageRankByLink(db1, 0.5);
//		}
//
//		if (pageUpdated == true) {
//			List<List<Double>> weightt = RankStem(db1, db2, 1);
//			List<List<Double>> weightb = RankStem(db1, db2, 2);
//
//			db4.updateEntry("weightt", weightt);
//			db4.updateEntry("weightb", weightb);
//		}
//
//		if (input == null) {
//			System.out.println("error: input in search engine is null so we will not give you any webpage");
//			return new ArrayList<>();
//		}
//
//		int checked = 1;
//		if (checkboxValue == null)
//			checked = 0;
//
//		List<Double> scoret = RankStemWithQuery(db1, db2, input, 1, db4.getEntry("weightt"),  stoppath);
//		List<Double> scoreb = RankStemWithQuery(db1, db2, input, 2, db4.getEntry("weightb"),  stoppath);
//		ArrayList<Double> scoretp = weightIncreaseByPhase(db1, db2, input,stopStem,1);
//		ArrayList<Double> scorebp = weightIncreaseByPhase(db1, db2, input,stopStem,2);
//		ArrayList<Double> pageScore = PageScoreByBoth(db1, checked, scoret, scoreb, scoretp, scorebp, 5.0, 3.0, 5.0, 3.0);
//		return pageScore;
//	}
//
//	// to help rank webnodes in jsp call PageRankByBoth
//	public static ArrayList<Integer> pageRanking(ArrayList<Double> pageScore) throws Exception {
//		System.out.println("pageScore for ranking " + pageScore);
//		ArrayList<Integer> resultRanking = PageRankByBoth(pageScore);
//		return resultRanking;
//	}
//
//	// to help rank webnodes in jsp by helping it get the corresponding webnode
//	public static ArrayList<WebNode> nodeRanking(ArrayList<Integer> resultRanking) throws Exception {
//		ArrayList<WebNode> resultRankedNodes = new ArrayList<>();
//		for (Integer rankpage : resultRanking)
//		{
//			WebNode currentWebNode = db1.getEntry(TreeNames.id2WebNode.toString(), rankpage, WebNode.class);
//			resultRankedNodes.add(currentWebNode);
//		}
//		return resultRankedNodes;
//	}
//
//	// to help print out keystems for each webpage in jsp
//	public static String nodeKeyWord(WebNode currentW) throws IOException {
//		Map<String, Integer> keyword2Freq;
//		keyword2Freq = db2.getKeywordFrequency(currentW.getId(), 20, 0);
//		StringBuilder sb = new StringBuilder();
//		keyword2Freq.forEach((k, v) -> sb.append(k).append(" ").append(v).append("; "));
//		return sb.toString();
//	}
//
//}
