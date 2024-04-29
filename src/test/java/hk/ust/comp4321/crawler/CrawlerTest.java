package hk.ust.comp4321.crawler;

import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrawlerTest {

    private IndexTable db;
    private Crawler crawler;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize your db and linkExtractor here
        db = new IndexTable("CrawlerTest");
        crawler = new Crawler(db);
    }

    @Test
    void testExtractLinks() throws Exception {
        System.out.println("Test extractLinks");
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
        int numPages = 200 ;
        long startTime = System.currentTimeMillis();

        List<String> result = crawler.extractLinks(root, numPages);
        long endTime = System.currentTimeMillis();
        result.forEach(System.out::println);
        assertNotNull(result);
        System.out.println("Size of result: " + result.size());
        Set<String> set = Set.of(result.toArray(new String[0]));
        System.out.println("Size of set: " + set.size());
        System.out.println("Page ID: " + db.getPageId());
        System.out.println("urld2Id size: " + IndexTable.getSize(db.getUrl2Id()));
        System.out.println("id2WebNode size: " + IndexTable.getSize(db.getId2WebNode()));
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
}