package hk.ust.comp4321.extractors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PageSizeExtractorTest {
    @Test
    void testExtractPageSize() {
        System.out.println("Test extractPageSize");
        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
        try {
            int result = PageSizeExtractor.extractPageSize(url);
            System.out.println("Page size: " + result);
            assertTrue(result > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}