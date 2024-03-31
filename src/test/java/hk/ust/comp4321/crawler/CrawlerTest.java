package hk.ust.comp4321.crawler;

import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
class CrawlerTest {
    private Crawler crawler;
    private IndexTable db;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize your db and crawler here
        db = new IndexTable("crawlerTest");
        crawler = new Crawler(db);
    }

    @Test
    @Order(1)
    void testExtractLinks() {
        System.out.println("Test extractLinks");
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
        int numPages = 20;

        List<String> result = crawler.extractLinks(root, numPages);
        result.forEach(System.out::println);
        assertNotNull(result);
    }

    @Test
    @Order(2)
    void testGetChildrenPageIds() {
        System.out.println("Test getChildrenPageIds");
        int id = 0; // assuming there is a WebNode with id 1 in the db

        List<Integer> result = crawler.getChildrenPageIds(id);
        result.forEach(System.out::println);

        System.out.println("Test getChildrenPageIds ends here");

        assertNotNull(result);
        // Add more assertions based on your expected result
    }

    @Test
    @Order(3)
    void testGetParentPageId() {
        System.out.println("Test getParentPageId");
        int id = 1; // assuming there is a WebNode with id 1 in the db

        List<Integer> result = crawler.getParentPageId(id);
        result.forEach(System.out::println);

        assertNotNull(result);
        // Add more assertions based on your expected result
    }
}