package hk.ust.comp4321.extractors;

import java.net.HttpURLConnection;
import java.net.URL;

public class LastModifiedDateExtractor {
    public static String extractModifiedDate(String url) throws Exception{
            // Create a URL object and open a connection to it
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Extract the last modified date from the HTTP header
            String lastModified = connection.getHeaderField("Last-Modified");
            return lastModified == null ? connection.getHeaderField("Date") : lastModified;
    }
}