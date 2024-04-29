package hk.ust.comp4321.extractors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LastModifiedDateExtractorTest {

    @Test
    public void testExtractModifiedDate() {
        try {
            String url = "https://www.example.com";
            String date = LastModifiedDateExtractor.extractModifiedDate(url);
            assertNotNull(date, "Date should not be null");
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testGetDateInMilliseconds() {
        String date = "Wed, 21 Oct 2015 07:28:00 GMT";
        long millis = LastModifiedDateExtractor.getDateInMilliseconds(date);
        assertNotNull(millis, "Milliseconds should not be null");
    }
}