package hk.ust.comp4321;

import hk.ust.comp4321.extractors.TitleExtractor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TitleExtractorTest {
    @Test
    public static void main(String[] args) {
//        String url = "http://www.cs.ust.hk/";
//        String url = "https://comp4321-hkust.github.io/testpages/testpage.htm";
//        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
        String title = null;
        try {
            title = TitleExtractor.extractTitle(url);
        } catch (Exception e) {
            System.out.printf("Error extracting title from %s\n", url);
            throw new RuntimeException(e);
        }
        System.out.println("Title: " + title);
        assertNotNull(title, "The extracted title should not be null");
    }
}
