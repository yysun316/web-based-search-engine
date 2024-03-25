package hk.ust.comp4321.extractors;

import org.htmlparser.util.ParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StringExtractorTest {

    @Test
    public void testExtractStrings() throws ParserException {
        String resource = "http://www.example.com";
        String expected = """
            Example Domain
            Example Domain
            This domain is for use in illustrative examples in documents. You may use this domain in literature without prior coordination or asking for permission.
            More information...""";
        String actual = StringExtractor.extractStrings(false, resource);

        // Remove separators from the expected and actual strings
        String expectedWithoutSeparators = expected.replaceAll("\\s+", "");
        String actualWithoutSeparators = actual.replaceAll("\\s+", "");
        System.out.println("Expected: " + expectedWithoutSeparators);
        System.out.println("Actual: " + actualWithoutSeparators);
        assertEquals(expectedWithoutSeparators, actualWithoutSeparators);
    }
}