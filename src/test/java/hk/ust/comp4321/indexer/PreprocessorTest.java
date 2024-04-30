package hk.ust.comp4321.indexer;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PreprocessorTest {
    private Crawler crawler;
    private IndexTable indexTable;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("IndexerTest");
        crawler = new Crawler(indexTable);
    }

    @Test
    public void testPreprocessor() throws ExecutionException, InterruptedException {
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        int numPages = 300;
        System.out.println("Extracting links from " + root + "...");
        long startTime1 = System.currentTimeMillis();
        List<String> links = crawler.extractLinks(root, numPages);
        long endTime1 = System.currentTimeMillis();
        System.out.println("Time taken to extract links: " + (endTime1 - startTime1) + "ms");
        assertNotNull(links);
        Preprocessor preprocessor = new Preprocessor(links, indexTable, "resources/stopwords.txt");
        FutureTask<List<HashMap<Integer, String[]>>> listFutureTask = new FutureTask<>(preprocessor);
        Thread thread = new Thread(listFutureTask);
        thread.start();
        List<HashMap<Integer, String[]>> result = listFutureTask.get();
        System.out.println("Time taken to preprocess data: " + (System.currentTimeMillis() - endTime1) + "ms");
        checkPreprocessedData(result);
    }

    public void checkPreprocessedData(List<HashMap<Integer, String[]>> result) {
        System.out.println("Checking the preprocessed data...");
        System.out.println("Number of title entries: " + result.get(0).size());
        System.out.println("Number of body entries: " + result.get(1).size());

        for (int i = 0; i < result.get(0).size(); i++) {
            System.out.println("Title of page " + i + ": " + String.join(" ", result.get(0).get(i)));
        }

        for (int i = 0; i < result.get(1).size(); i++) {
            System.out.println("Body of page " + i + ": " + String.join(" ", result.get(1).get(i)));
        }
    }
}
