package hk.ust.comp4321.extractors;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import java.net.URL;

public class TitleExtractor {
    public static String extractTitle(String url) throws Exception {
            // Create a Parser object and fetch the HTML content from the URL
            Parser parser = new Parser(new URL(url).openConnection());
            NodeList nodeList = parser.extractAllNodesThatMatch(new TagNameFilter("title"));

            // Extract the title from the NodeList
            if (nodeList.size() > 0) {
                String title = nodeList.elementAt(0).toPlainTextString();
                return title;
            }
        return "";
    }
}
