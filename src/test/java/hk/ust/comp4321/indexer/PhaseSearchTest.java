package hk.ust.comp4321.indexer;

import hk.ust.comp4321.crawler.Crawler;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhaseSearchTest {
    private Indexer indexer;
    private Crawler crawler;
    private ForwardInvertedIndex forwardInvertedIndex;
    private IndexTable indexTable;
    private StopStem stopStem;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("IndexerTest");
        crawler = new Crawler(indexTable);
        forwardInvertedIndex = new ForwardInvertedIndex("ForwardInvertedIndexTest");
        indexer = new Indexer(forwardInvertedIndex);
    }

    @Test
    void testGetPhases() {
        // Initialize StopStem object
        StopStem stopStem = new StopStem("resources/stopwords.txt");

        // Define a test query
        String query = "\"This is a test\" \"Another test phrase\"";

        // Expected result after stop word and stemming process
        ArrayList<String> expected = new ArrayList<>(Arrays.asList("thi is a test", "anoth test phrase"));

        // Call the method to test
        ArrayList<String> result = PhasesSearch.getPhases(query, stopStem);

        // Check if the result matches the expected output
        assertEquals(expected, result);
    }

    @Test
    void testWeightIncreaseByPhase() throws IOException, ExecutionException, InterruptedException {
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse/UG.htm";
        int numPages = 1;
        List<String> links = crawler.extractLinks(root, numPages);
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
        String query = "\"hong kong\"";
        ArrayList<Double> expected = new ArrayList<>(Arrays.asList(6.0));
        ArrayList<Double> result =
                PhasesSearch.weightIncreaseByPhase
                        (indexTable, forwardInvertedIndex, query, stopStem, 2);
        Assertions.assertEquals(expected, result, "The result is correct.");
    }
}