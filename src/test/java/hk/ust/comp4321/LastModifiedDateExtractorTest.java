package hk.ust.comp4321;

import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class LastModifiedDateExtractorTest {
    @Test
    public void testExtractModifiedDate() {
        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm"; // replace with a URL of a webpage that contains a "Last-Modified" tag
        String date = null;
        try {
            date = LastModifiedDateExtractor.extractModifiedDate(url);
        } catch (Exception e) {
            System.out.println("Error extracting last modified date");
            throw new RuntimeException(e);
        }
        System.out.println("Last modified date: " + date); // Last-Modified :Tue, 16 May 2023 05:03:16 GMT
        assertNotNull(date, "The extracted date should not be null");
    }

}
