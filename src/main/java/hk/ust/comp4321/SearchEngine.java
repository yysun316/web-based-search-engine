package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.PhasesSearch;
import hk.ust.comp4321.indexer.Preprocessor;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;
import hk.ust.comp4321.utils.WeightDataStorage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import static hk.ust.comp4321.utils.PageRank.*;


@WebServlet("/add")
public class SearchEngine extends HttpServlet {
    private static StopStem stopStem;
    private static Crawler crawler;
    private static IndexTable indexTable;
    private static ForwardInvertedIndex forwardInvertedIndex;
    private static WeightDataStorage weightDataStorage;
    private static Indexer indexer;

    // the main function to be called by jsp
    public static ArrayList<Double> processInput(String input, String checkboxValue, String stopPath) throws Exception {
        input = input.toLowerCase();
        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";

        stopStem = new StopStem(stopPath);
        try {
            indexTable = new IndexTable("indexTable");
            forwardInvertedIndex = new ForwardInvertedIndex("forwardInvertedIndex");
            weightDataStorage = new WeightDataStorage("weightDataStorage");
            indexer = new Indexer(forwardInvertedIndex);
            crawler = new Crawler(indexTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int numPages = 300;
        System.out.println("Extracting links from " + rootURL + "...");
        long startTime1 = System.currentTimeMillis();
        List<String> links = crawler.extractLinks(rootURL, numPages);
        long endTime1 = System.currentTimeMillis();
        System.out.println("Time taken to extract links: " + (endTime1 - startTime1) + "ms");
        long endTime3 = endTime1;
        if (!links.isEmpty()) {
            Preprocessor preprocessor = new Preprocessor(links, indexTable, stopPath);
            FutureTask<List<HashMap<Integer, String[]>>> listFutureTask = new FutureTask<>(preprocessor);
            Thread thread = new Thread(listFutureTask);
            thread.start();
            List<HashMap<Integer, String[]>> preprocessedData = listFutureTask.get();
            long endTime2 = System.currentTimeMillis();
            System.out.println("Time taken to preprocess data: " + (endTime2 - endTime1) + "ms");

            indexer.setProcessedData(preprocessedData);
            Thread thread1 = new Thread(indexer);
            thread1.start();
            thread1.join(); // May change later
            endTime3 = System.currentTimeMillis();
            System.out.println("Time taken to index data: " + (endTime3 - endTime2) + "ms");
        }
        boolean pageUpdated = false;
        if (!links.isEmpty())
        {
            pageUpdated = true;
            PageRankByLink(indexTable, 0.5);
        }
        long endTime4 = System.currentTimeMillis();
        System.out.println("Time taken to update page rank by link: " + (endTime4 - endTime3) + "ms");

        if (pageUpdated) {
            List<List<Double>> weightt = RankStem(indexTable, forwardInvertedIndex, 1);
            List<List<Double>> weightb = RankStem(indexTable, forwardInvertedIndex, 2);

            weightDataStorage.updateEntry("weightt", weightt);
            weightDataStorage.updateEntry("weightb", weightb);
        }
        long endTime5 = System.currentTimeMillis();
        System.out.println("Time taken to update weightDataStorage: " + (endTime5 - endTime4) + "ms");

        if (input == null) {
            System.out.println("error: input in search engine is null so we will not give you any webpage");
            return new ArrayList<>();
        }
        int checked = 1;
        if (checkboxValue == null)
            checked = 0;
        List<Double> scoret = RankStemWithQuery(indexTable, forwardInvertedIndex, input, 1, weightDataStorage.getEntry("weightt"), stopPath);
        List<Double> scoreb = RankStemWithQuery(indexTable, forwardInvertedIndex, input, 2, weightDataStorage.getEntry("weightb"), stopPath);
        long endTime6 = System.currentTimeMillis();
        System.out.println("Time taken to rank stem with query: " + (endTime6 - endTime5) + "ms");
        ArrayList<Double> scoretp = PhasesSearch.weightIncreaseByPhase(indexTable, forwardInvertedIndex, input, stopStem, 1);
        ArrayList<Double> scorebp = PhasesSearch.weightIncreaseByPhase(indexTable, forwardInvertedIndex, input, stopStem, 2);
        long endTime7 = System.currentTimeMillis();
        System.out.println("Time taken to rank weight increase by phase: " + (endTime7 - endTime6) + "ms");
        ArrayList<Double> pageScore = PageScoreByBoth(indexTable, checked, scoret, scoreb, scoretp, scorebp, 5.0, 3.0, 5.0, 3.0);
        return pageScore;
    }

    /***
     * Rank the webnodes by pageRank
     * @param pageScore the pageScore of the webnodes
     * @return the ranking of the webnodes
     * @throws Exception if the pageScore is not found
     */
    public static ArrayList<Integer> pageRanking(ArrayList<Double> pageScore) throws Exception {
        System.out.println("pageScore for ranking " + pageScore);
        ArrayList<Integer> resultRanking = PageRankByBoth(pageScore);
        return resultRanking;
    }

    /***
     * Get the webnodes from the ranking
     * @param resultRanking the ranking of the webnodes
     * @return the webnodes
     * @throws Exception if the webnodes are not found
     */
    public static ArrayList<WebNode> nodeRanking(ArrayList<Integer> resultRanking) throws Exception {
        ArrayList<WebNode> resultRankedNodes = new ArrayList<>();
        for (Integer rankpage : resultRanking) {
            WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), rankpage, WebNode.class);
            resultRankedNodes.add(currentWebNode);
        }
        return resultRankedNodes;
    }

    /***
     * Get the keyword of the node
     * @param currentW the node
     * @return the keyword of the node
     * @throws IOException if the keyword is not found
     */
    public static String nodeKeyWord(WebNode currentW) throws IOException {
        Map<String, Integer> keyword2Freq;
        keyword2Freq = forwardInvertedIndex.getKeywordFrequency(currentW.getId(), 20, 0);
        StringBuilder sb = new StringBuilder();
        keyword2Freq.forEach((k, v) -> sb.append(k).append(" ").append(v).append("; "));
        return sb.toString();
    }

}
