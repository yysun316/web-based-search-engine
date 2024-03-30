package hk.ust.comp4321.indexer;

import hk.ust.comp4321.extractors.StringExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;

import java.util.ArrayList;
import java.util.Hashtable;

public class Indexer {
    StopStem stopStem;
    IndexTable indexTable;

    public Indexer(IndexTable indexTable) {
        this.indexTable = indexTable;
        stopStem = new StopStem("resources/stopwords.txt");
    }

    public void index(String url) throws Exception {
        // Index the title
        indexTitle(url);
        indexBody(url);
    }

    private void indexTitle(String url) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
//        System.out.println("pageId: " + pageId);
        if (pageId == -1)
            throw new Exception("Page ID not found");

        // 1. Extract words from the title
        String[] words = TitleExtractor.extractTitle(url).split("[\\t\\s\\p{Punct}]+"); // split by whitespace
//        System.out.println("Title: " + Arrays.toString(words));
        int pos = 0; // position of the word in the title
        Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdTitle -> positions
        // 2. Remove stop words and stem the words
        for (String word : words) {
            if (!stopStem.isStopWord(word)) {
                String stem = stopStem.stem(word);
                // 3. If stem exists in the word2IdTitle table, get the wordId; otherwise, add the stem to the tables
                if (indexTable.getWordIdTitleFromStem(stem) == -1) {
                    //System.out.println("stem is " + stem + " indexTable.getWordIdTitle() is "+ indexTable.getWordIdTitle());
                    //System.out.println("indexTable.getWordIdTitle() is "+ indexTable.getWordIdTitle());
                    indexTable.addEntry(TreeNames.word2IdTitle.toString(), stem, indexTable.getWordIdTitle());
                    //indexTable.addEntry(TreeNames.IdTitle2Word.toString(), indexTable.getWordIdTitleFromStem(stem), stem);
//                    System.out.println(stem + " not found in word2IdTitle table. Adding...");
                }
//                // 4. Find all the positions of the stem in the title
//                int wordIdTitle = indexTable.getWordIdTitleFromStem(stem);
////                System.out.println(stem + " has wordId: " + wordIdTitle);
//                ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdTitle, k -> new ArrayList<>());
//                tmpPos.add(pos);
            }
            pos++;
        }
//        // 5. Insert the wordIdTitle and positions into the inverted index
//        for (int wordIdTitle : wordId2Pos.keySet()) {
//            indexTable.updateInvertedIdx(TreeNames.invertedIdxTitle.toString(), wordIdTitle, pageId, wordId2Pos.get(wordIdTitle).size(), wordId2Pos.get(wordIdTitle));
//        }
////         6. Insert the wordIdTitle and id into the forward index
//        for (int wordIdTitle : wordId2Pos.keySet()) {
//            indexTable.updateForwardIdx(TreeNames.forwardIdxTitle.toString(), pageId, wordIdTitle, wordId2Pos.get(wordIdTitle).size(), wordId2Pos.get(wordIdTitle));
//        }
    }

    private void indexBody(String url) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
//        System.out.println("pageId: " + pageId);
        if (pageId == -1)
            throw new Exception("Page ID not found");
        // 1. Extract words from the body
        String[] words = StringExtractor.extractStrings(false, url);
//        System.out.println("Body: " + Arrays.toString(words));
        int pos = 0; // position of the word in the body
        Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdBody -> positions
//        // 2. Remove stop words and stem the words
        for (String word : words) {
            if (!stopStem.isStopWord(word)) {
                String stem = stopStem.stem(word);
                // 3. If stem exists in the word2IdBody table, get the wordId; otherwise, add the stem to the tables
                if (indexTable.getWordIdBodyFromStem(stem) == -1) {
                    indexTable.addEntry(TreeNames.word2IdBody.toString(), stem, indexTable.getWordIdBody());
                    indexTable.addEntry(TreeNames.IdBody2Word.toString(), indexTable.getWordIdBodyFromStem(stem), stem);
//                    System.out.println(stem + " not found in word2IdBody table. Adding...");
                }
                // 4. Find all the positions of the stem in the body
//                int wordIdBody = indexTable.getWordIdBodyFromStem(stem);
////                System.out.println(stem + " has wordId: " + wordIdBody);
//                ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdBody, k -> new ArrayList<>());
//                tmpPos.add(pos);
            }
            pos++;
        }
//        // 5. Insert the wordIdBody and positions into the inverted index
//        for (int wordIdBody : wordId2Pos.keySet()) {
//            indexTable.updateInvertedIdx(TreeNames.invertedIdxBody.toString(), wordIdBody, pageId, wordId2Pos.get(wordIdBody).size(), wordId2Pos.get(wordIdBody));
//        }
//        // 6. Insert the wordIdBody and id into the forward index
//        for (int wordIdBody : wordId2Pos.keySet()) {
//            indexTable.updateForwardIdx(TreeNames.forwardIdxBody.toString(), pageId, wordIdBody, wordId2Pos.get(wordIdBody).size(), wordId2Pos.get(wordIdBody));
//        }
    }
}
