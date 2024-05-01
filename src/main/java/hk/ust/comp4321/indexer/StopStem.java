package hk.ust.comp4321.indexer;


import hk.ust.comp4321.utils.Porter;

import java.io.*;
import java.util.HashSet;
public class StopStem {
    private Porter porter;
    private HashSet<String> stopWords;

    /***
     * Check if the word is a stop word
     * @param str the word
     * @return true if the word is a stop word, false otherwise
     */
    public boolean isStopWord(String str) {
        return stopWords.contains(str);
    }

    /***
     * Constructor of StopStem
     * @param str the path of the stop words
     */
    public StopStem(String str) {
        super();
        porter = new Porter();
        stopWords = new HashSet<>();
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(str);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found, please check the path in the constructor of StopStem");
        }
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the file, please check the path in the constructor of StopStem");
        }
    }

    /***
     * Stem the word
     * @param str the word
     * @return the stemmed word
     */
    public String stem(String str) {
        return porter.stripAffixes(str);
    }
}
