package hk.ust.comp4321.extractors;

import java.net.HttpURLConnection;
import java.net.URL;

import static java.util.Date.parse;

public class LastModifiedDateExtractor {
    public static String extractModifiedDate(String url) throws Exception{
            // Create a URL object and open a connection to it
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Extract the last modified date from the HTTP header
            String lastModified = connection.getHeaderField("Last-Modified");
            return lastModified == null ? connection.getHeaderField("Date") : lastModified;
    }

    public static long getDateInMilliseconds(String date) {
        // Convert the date string to milliseconds
        return parse(date);
    }
}
