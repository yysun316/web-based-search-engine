package hk.ust.comp4321.extractors;

import java.net.HttpURLConnection;
import java.net.URL;

import static java.util.Date.parse;

public class LastModifiedDateExtractor {
    /***
     * Extract the last modified date of a webpage
     * @param url the URL of the webpage
     * @return the last modified date of the webpage
     * @throws Exception if the URL is invalid
     */
    public static String extractModifiedDate(String url) throws Exception{
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            String lastModified = connection.getHeaderField("Last-Modified");
            return lastModified == null ? connection.getHeaderField("Date") : lastModified;
    }
    /***
     * Convert the date string to milliseconds
     * @param date the date string
     * @return the date in milliseconds
     */
    public static long getDateInMilliseconds(String date) {
        return parse(date);
    }
}
