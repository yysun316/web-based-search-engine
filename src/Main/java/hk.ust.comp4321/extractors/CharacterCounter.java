package hk.ust.comp4321.extractors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class CharacterCounter {
    public int countCharacters(String webpageUrl) throws IOException {
        URL url = new URL(webpageUrl);
        URLConnection connection = url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" ");
            }
            return (countCharactersInText(content.toString())-1);
        }
    }

    private int countCharactersInText(String text) {
        return text.length();
    }
}



//package hk.ust.comp4321.extractors;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//
//public class CharacterCounter {
//    public int countCharacters(String webpageUrl) throws IOException {
//        URL url = new URL(webpageUrl);
//        URLConnection connection = url.openConnection();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//            StringBuilder content = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append(" ");
//            }
//            return (countCharactersInText(content.toString()));
//        }
//    }
//
//    private int countCharactersInText(String text) {
//        return text.length();
//    }
//}