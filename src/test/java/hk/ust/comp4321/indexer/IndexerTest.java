package hk.ust.comp4321.indexer;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IndexerTest {
    private Indexer indexer;
    private String stopPath = "resources/stopwords.txt";
    private Crawler crawler;
    private ForwardInvertedIndex forwardInvertedIndex;
    private IndexTable indexTable;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("IndexerTest");
        crawler = new Crawler(indexTable);
        forwardInvertedIndex = new ForwardInvertedIndex("ForwardInvertedIndexTest");
        indexer = new Indexer(indexTable, forwardInvertedIndex, stopPath);
    }

    @Test
    public void testIndex() {
        try {
            String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
            int numPages = 300;
            System.out.println("Extracting links from " + root + "...");
            long startTime1 = System.currentTimeMillis();
            List<String> links = crawler.extractLinks(root, numPages);
            long endTime1 = System.currentTimeMillis();
            System.out.println("Time taken to extract links: " + (endTime1 - startTime1) + "ms");
            assertNotNull(links);

            Preprocessor preprocessor = new Preprocessor(links, indexTable);
            FutureTask<List<HashMap<Integer, String[]>>> listFutureTask = new FutureTask<>(preprocessor);
            Thread thread = new Thread(listFutureTask);
            thread.start();
            List<HashMap<Integer, String[]>> result = listFutureTask.get();
            indexer.setProcessedData(result);
            indexer.start();
            indexer.join();
            checkDB();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkDB() throws IOException {
        System.out.println("Checking the database...");
        for (int i = 0; i < indexTable.getPageId(); i++) {
            System.out.println("Number of entries in the forward inverted index: " + forwardInvertedIndex.getKeywordFrequency(i, 10, -1));
        }

    }
}