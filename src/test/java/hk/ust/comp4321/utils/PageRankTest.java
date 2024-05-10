package hk.ust.comp4321.utils;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.Preprocessor;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;

import static hk.ust.comp4321.indexer.PhrasesSearch.weightIncreaseByPhrase;
import static hk.ust.comp4321.utils.PageRank.*;

class PageRankTest {

    private Indexer indexer;
    private Crawler crawler;
    private ForwardInvertedIndex forwardInvertedIndex;
    private IndexTable indexTable;
    private StopStem stopStem;
    private WeightDataStorage weightDataStorage;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("IndexerTest");
        crawler = new Crawler(indexTable);
        forwardInvertedIndex = new ForwardInvertedIndex("ForwardInvertedIndexTest");
        indexer = new Indexer(forwardInvertedIndex);
        weightDataStorage = new WeightDataStorage("weightDataStorage");
    }

    @Test
    void testRanking() throws Exception {
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        int numPages = 1;
        List<String> links = crawler.extractLinks(root, numPages);
        PageRankByLink(indexTable, 0.85);
        System.out.println("links are: " + links);
        if (!links.isEmpty()) {
            Preprocessor preprocessor = new Preprocessor(links, indexTable, "resources/stopwords.txt");
            FutureTask<List<HashMap<Integer, String[]>>> listFutureTask = new FutureTask<>(preprocessor);
            Thread thread = new Thread(listFutureTask);
            thread.start();
            List<HashMap<Integer, String[]>> preprocessedData = listFutureTask.get();
            indexer.setProcessedData(preprocessedData);
            Thread thread1 = new Thread(indexer);
            thread1.start();
            thread1.join(); // May change later
        }
        stopStem = new StopStem("resources/stopwords.txt");
        String query = "test";
        List<List<Double>> weightt = RankStem(indexTable, forwardInvertedIndex, 1);
        List<List<Double>> weightb = RankStem(indexTable, forwardInvertedIndex, 2);
        weightDataStorage.updateEntry("weightt", weightt);
        weightDataStorage.updateEntry("weightb", weightb);
        List<Double> scoret = RankStemWithQuery(indexTable, forwardInvertedIndex, query, 1, weightDataStorage.getEntry("weightt"), "resources/stopwords.txt");
        List<Double> scoreb = RankStemWithQuery(indexTable, forwardInvertedIndex, query, 2, weightDataStorage.getEntry("weightb"), "resources/stopwords.txt");
        ArrayList<Double> scoretp = weightIncreaseByPhrase(indexTable, forwardInvertedIndex, query, stopStem, 1);
        ArrayList<Double> scorebp = weightIncreaseByPhrase(indexTable, forwardInvertedIndex, query, stopStem, 2);
        ArrayList<Double> pageScore = PageScoreByBoth(indexTable, 1, scoret, scoreb, scoretp, scorebp, 5.0, 3.0, 5.0, 3.0);
        ArrayList<Integer> resultRanking = PageRankByBoth(pageScore);
        ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(0));
        Assertions.assertEquals(expected, resultRanking, "The result  is correct");
    }

}