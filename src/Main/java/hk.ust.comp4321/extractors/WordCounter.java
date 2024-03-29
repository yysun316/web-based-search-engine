package hk.ust.comp4321.extractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WordCounter {
    public int countWords(String webpageUrl) throws IOException {
        URL url = new URL(webpageUrl);
        URLConnection connection = url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" ");
            }
            return countWordsInText(content.toString());
        }
    }

    private int countWordsInText(String text) {
        String[] words = text.trim().split("\\s+");
        return words.length;
    }
}
