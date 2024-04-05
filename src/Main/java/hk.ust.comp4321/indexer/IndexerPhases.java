package hk.ust.comp4321.indexer;

import hk.ust.comp4321.extractors.StringExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class IndexerPhases {
    private static IndexTable indexTable;
    private static ForwardInvertedIndex forwardInvertedIndex;

    public IndexerPhases(IndexTable indexTable, ForwardInvertedIndex forwardInvertedIndex) {
        this.indexTable = indexTable;
        this.forwardInvertedIndex = forwardInvertedIndex;
    }

    public void indexPhases(String url, Integer phalen) throws Exception {
        // Index the title
        indexTitlePhases(url, phalen);
        indexBodyPhases(url, phalen);
    }

    private void indexTitlePhases(String url, Integer phalen) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
//        System.out.println("pageId: " + pageId);
        if (pageId == -1) throw new Exception("Page ID not found");

        // 1. Extract words from the title
        String[] words = Arrays.stream(TitleExtractor.extractTitle(url).split("[\\s\\p{Punct}&&[^-]]+")).filter(word -> word != null && !word.trim().isEmpty()).toArray(String[]::new); // split by whitespace
        if (words.length < 1) return;
//        System.out.println("Title: " + Arrays.toString(words));
        Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdTitle -> positions
        // 2. Remove stop words and stem the words
        for (int wordpos = 0; wordpos <= (words.length-phalen); wordpos++)
        {
            String stem ="";
            for (int addwd = 0; addwd < phalen; addwd++)
            {
                stem = stem + words[wordpos].toLowerCase();
            }

            // 3. If stem exists in the word2IdTitle table, get the wordId; otherwise, add the stem to the tables
            if (forwardInvertedIndex.getWordIdTitleFromStem(stem) == -1) {
                forwardInvertedIndex.addEntry(TreeNames.word2IdTitle.toString(), stem, forwardInvertedIndex.getWordIdTitle());
                forwardInvertedIndex.addEntry(TreeNames.IdTitle2Word.toString(), forwardInvertedIndex.getWordIdTitleFromStem(stem), stem);
//                    System.out.println(stem + " not found in word2IdTitle table. Adding...");
            }
            // 4. Find all the positions of the stem in the title
            int wordIdTitle = forwardInvertedIndex.getWordIdTitleFromStem(stem);
//                System.out.println(stem + " has wordId: " + wordIdTitle);
            ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdTitle, k -> new ArrayList<>());
            tmpPos.add(wordpos-1);
        }
    }

    private void indexBodyPhases(String url, Integer phalen) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
//        System.out.println("pageId: " + pageId);
        if (pageId == -1) throw new Exception("Page ID not found");
        // 1. Extract words from the body
        String[] words = StringExtractor.extractStrings(false, url);
        if (words.length == 0) return;
//        System.out.println("Body: " + Arrays.toString(words));
        Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdBody -> positions
        // 2. Remove stop words and stem the words
        for (int wordpos = 0; wordpos <= (words.length-phalen); wordpos++)
        {
            String stem ="";
            for (int addwd = 0; addwd < phalen; addwd++)
            {
                stem = stem + words[wordpos].toLowerCase();
            }

            // 3. If stem exists in the word2IdBody table, get the wordId; otherwise, add the stem to the tables
            if (forwardInvertedIndex.getWordIdBodyFromStem(stem) == -1) {
                forwardInvertedIndex.addEntry(TreeNames.word2IdBody.toString(), stem, forwardInvertedIndex.getWordIdBody());
                forwardInvertedIndex.addEntry(TreeNames.IdBody2Word.toString(), forwardInvertedIndex.getWordIdBodyFromStem(stem), stem);
//                    System.out.println(stem + " not found in word2IdBody table. Adding...");
            }
            // 4. Find all the positions of the stem in the body
            int wordIdBody = forwardInvertedIndex.getWordIdBodyFromStem(stem);
//                System.out.println(stem + " has wordId: " + wordIdBody);
            ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdBody, k -> new ArrayList<>());
            tmpPos.add(wordpos-1);
        }
    }
}