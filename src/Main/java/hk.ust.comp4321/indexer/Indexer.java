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
        if (pageId == -1)
            throw new Exception("Page ID not found");

        // 1. Extract words from the title
        String[] words = TitleExtractor.extractTitle(url).split("[\\t\\s\\p{Punct}]+"); // split by whitespace
        int pos = 0; // position of the word in the title
        Hashtable<Integer, ArrayList<Integer>> res = new Hashtable<>(); // wordIdTitle -> positions
        // 2. Remove stop words and stem the words
        for (String word : words) {
            if (!stopStem.isStopWord(word)) {
                String stem = stopStem.stem(word);
                // 3. If stem exists in the word2IdTitle table, get the wordId; otherwise, add the stem to the tables
                if (indexTable.getWordIdTitleFromStem(stem) == -1) {
                    indexTable.addEntry(TreeNames.word2IdTitle.toString(), stem, indexTable.getWordIdTitle());
                    indexTable.addEntry(TreeNames.IdTitle2Word.toString(), indexTable.getWordIdTitle(), stem);
                }
                // 4. Find all the positions of the stem in the title
                int wordIdTitle = indexTable.getWordIdTitleFromStem(stem);
                ArrayList<Integer> tmpPos = res.get(wordIdTitle);
                if (tmpPos == null) {
                    tmpPos = new ArrayList<>();
                    res.put(wordIdTitle, tmpPos);
                }
                tmpPos.add(pos);
            }
            pos++;
        }
        // 5. Insert the wordIdTitle and positions into the inverted index
        for (int wordIdTitle : res.keySet()) {
            indexTable.updateInvertedIdx(TreeNames.invertedIdxTitle.toString(), wordIdTitle, pageId, res.get(wordIdTitle).size());
        }
        // 6. Insert the wordIdTitle and id into the forward index
        for (int wordIdTitle : res.keySet()) {
            indexTable.updateForwardIdx(TreeNames.forwardIdxTitle.toString(), pageId, wordIdTitle, res.get(wordIdTitle));
        }
    }

    private void indexBody(String url) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
        if (pageId == -1)
            return;
        // 1. Extract words from the body
        String[] words = StringExtractor.extractStrings(false, url);
        int pos = 0; // position of the word in the body
        Hashtable<Integer, ArrayList<Integer>> res = new Hashtable<>(); // wordIdBody -> positions
        // 2. Remove stop words and stem the words
        for (String word : words) {
            if (!stopStem.isStopWord(word)) {
                String stem = stopStem.stem(word);
                // 3. If stem exists in the word2IdBody table, get the wordId; otherwise, add the stem to the tables
                if (indexTable.getWordIdBodyFromStem(stem) == -1) {
                    indexTable.addEntry(TreeNames.word2IdBody.toString(), stem, indexTable.getWordIdBody());
                    indexTable.addEntry(TreeNames.IdBody2Word.toString(), indexTable.getWordIdBody(), stem);
                }
                // 4. Find all the positions of the stem in the body
                int wordIdBody = indexTable.getWordIdBodyFromStem(stem);
                ArrayList<Integer> tmpPos = res.get(wordIdBody);
                if (tmpPos == null) {
                    tmpPos = new ArrayList<>();
                    res.put(wordIdBody, tmpPos);
                }
                tmpPos.add(pos);
            }
            pos++;
        }
    }
}
