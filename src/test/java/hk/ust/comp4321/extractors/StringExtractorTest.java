package hk.ust.comp4321.extractors;

import org.junit.jupiter.api.Test;

public class StringExtractorTest {

//    @Test
//    public void testExtractStrings() throws ParserException {
//        String resource = "http://www.example.com";
//        String expected = """
//            Example Domain
//            Example Domain
//            This domain is for use in illustrative examples in documents. You may use this domain in literature without prior coordination or asking for permission.
//            More information...""";
//        String actual = StringExtractor.extractStrings(false, resource);

//        String expectedWithoutSeparators = expected.replaceAll("\\s+", "");
//        String actualWithoutSeparators = actual.replaceAll("\\s+", "");
//        System.out.println("Expected: " + expectedWithoutSeparators);
//        System.out.println("Actual: " + actualWithoutSeparators);
//        assertEquals(expectedWithoutSeparators, actualWithoutSeparators);
//    }
    @Test
    public void testExtractStrings() throws Exception {
        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
//        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm"; // replace with a URL of a webpage that contains a "Last-Modified" tag
        String[] words = StringExtractor.extractStrings(false, url);
        for (String word : words) {
            System.out.println(word);
        }
    }
}