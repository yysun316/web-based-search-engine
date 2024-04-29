package hk.ust.comp4321.extractors;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import java.net.URL;

/***
 * The class to extract the title of a webpage

 */
public class TitleExtractor {
    /***
     * Extract the title of a webpage
     * @param url the URL of the webpage
     * @return the title of the webpage
     * @throws Exception if the title cannot be extracted
     */
    public static String extractTitle(String url) throws Exception {
            // Create a Parser object and fetch the HTML content from the URL
            Parser parser = new Parser(new URL(url).openConnection());
            NodeList nodeList = parser.extractAllNodesThatMatch(new TagNameFilter("title"));

            // Extract the title from the NodeList
            if (nodeList.size() > 0)
                return nodeList.elementAt(0).toPlainTextString();
        return "";
    }
}
