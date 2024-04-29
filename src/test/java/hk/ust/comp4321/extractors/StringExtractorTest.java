package hk.ust.comp4321.extractors;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class StringExtractorTest {

    @Test
    void testExtractStrings() throws Exception {
        System.out.println("Test extractStrings");
        String link = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
        String[] result = StringExtractor.extractStrings(false, link);
        Arrays.stream(result).forEach(System.out::println);
        assert(result.length > 0);
    }

}