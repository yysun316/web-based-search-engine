package hk.ust.comp4321;

import hk.ust.comp4321.extractors.TitleExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TitleExtractorTest {
    @Test
    public static void main(String[] args) {
        String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
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
