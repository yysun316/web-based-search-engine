package hk.ust.comp4321.extractors;

import java.net.HttpURLConnection;
import java.net.URL;

/***
 * The class to extract the page size of a webpage
 */
public class PageSizeExtractor {
    /***
     * Extract the page size of a webpage
     * @param url the URL of the webpage
     * @return the page size of the webpage
     * @throws Exception if the page size cannot be extracted
     */
    public static int extractPageSize(String url) throws Exception {
        // Create a URL object and open a connection to it
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        // Extract the last modified date from the HTTP header
        String pageSize = connection.getHeaderField("Content-Length");

        return pageSize == null ? StringExtractor.extractStrings(false, url).length : Integer.parseInt(pageSize);
    }
}
