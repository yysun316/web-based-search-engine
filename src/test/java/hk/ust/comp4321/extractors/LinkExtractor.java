package hk.ust.comp4321.extractors;

import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LinkExtractorTest {

    @Test
    void testExtractLinks() throws Exception {
        // Create an instance of IndexTable
        IndexTable indexTable = new IndexTable("record");

        // Define the root URL and number of pages to extract
        String rootURL = "http://example.com";
        int numPages = 5;

        // Call the extractLinks method
        List<String> result = LinkExtractor.extractLinks(indexTable, rootURL, numPages);
        for (String res : result)
            System.out.println(res);

        // Check that the result is not null
        assertNotNull(result);

        // Check that the correct number of pages was extracted
        assertEquals(numPages, result.size());

        // Check that the first URL is the root URL
        assertEquals(rootURL, result.get(0));
    }
}
