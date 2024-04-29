package hk.ust.comp4321.extractors;

import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LinkExtractorTest {
    private IndexTable db;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize your db and linkExtractor here
        db = new IndexTable("linkExtractorTest");
    }

    @Test
    void testExtractLinks() throws Exception {
        System.out.println("Test extractLinks");
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
        int numPages = 300;
        long startTime = System.currentTimeMillis();

        List<String> result = LinkExtractor.extractLinks(db, root, numPages);
        long endTime = System.currentTimeMillis();
        result.forEach(System.out::println);
        assertNotNull(result);
        System.out.println("Size of result: " + result.size());
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

}