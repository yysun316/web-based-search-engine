package hk.ust.comp4321.extractors;

import java.net.HttpURLConnection;
import java.net.URL;

public class PageSizeExtractor {
    public static int extractPageSize(String url) throws Exception {
        // Create a URL object and open a connection to it
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        // Extract the last modified date from the HTTP header
        String pageSize = connection.getHeaderField("Content-Length");

        return pageSize == null ? StringExtractor.extractStrings(false, url).length : Integer.parseInt(pageSize);
    }
}
