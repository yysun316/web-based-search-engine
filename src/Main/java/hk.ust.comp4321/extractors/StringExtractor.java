package hk.ust.comp4321.extractors;

import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

/**
 * Extract plaintext strings from a web page.
 * Illustrative program to gather the textual contents of a web page.
 * Uses a {@link org.htmlparser.beans.StringBean StringBean} to accumulate
 * the user visible text (what a browser would display) into a single string.
 */
public class StringExtractor {
    /**
     * Extract the text from a page.
     *
     * @param links if <code>true</code> include hyperlinks in output.
     * @return The textual contents of the page.
     * @throws ParserException If a parse error occurs.
     */
    public static String extractStrings(boolean links, String resource) throws ParserException {
        StringBean sb;

        sb = new StringBean();
        sb.setLinks(links);
        sb.setURL(resource);

        return (sb.getStrings());
    }
}
