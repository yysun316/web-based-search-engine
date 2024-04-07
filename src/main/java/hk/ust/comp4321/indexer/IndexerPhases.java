package hk.ust.comp4321.indexer;

import hk.ust.comp4321.extractors.StringExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import jdbm.btree.BTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringJoiner;


public class IndexerPhases {
    // Given a pageId, how many phrases and their corresponding frequency
    // pageId -> (phrases, frequency) (BTree) where did u store
    private static IndexTable indexTable;
    private static StopStem stopStem;
    private static ForwardInvertedIndex forwardInvertedIndex; //db2
    private static ForwardInvertedIndex forwardInvertedIndexPhrase;// db3

    public IndexerPhases(IndexTable indexTable, ForwardInvertedIndex forwardInvertedIndex, ForwardInvertedIndex forwardInvertedIndexPhrase, String stoppath) {
        this.indexTable = indexTable;
        this.forwardInvertedIndex = forwardInvertedIndex;
        this.forwardInvertedIndexPhrase = forwardInvertedIndexPhrase;
        stopStem = new StopStem(stoppath);
    }

    public void indexPhases(String url, int maxAllowedLength) throws Exception {
        // Index the title
        indexTitle(url, maxAllowedLength);
        indexBody(url, maxAllowedLength);
    }

    public void indexTitle(String url, int maxAllowedLength) throws Exception {
        //System.out.println("Enter index title");
        int pageId = indexTable.getPageIdFromURL(url);
        //System.out.println("pageId: " + pageId);
        if (pageId == -1) throw new Exception("Page ID not found");
        // 1. Extract words from the title
        String[] words = Arrays.stream(TitleExtractor.extractTitle(url).split("[\\s\\p{Punct}&&[^-]]+"))
                .filter(word -> word != null && !word.trim().isEmpty())
                .map(String::toLowerCase)
                .toArray(String[]::new);
        if (words.length == 0) return;

        for (int i = 0; i < words.length; i++) {
            words[i] = stopStem.stem(words[i].toLowerCase());
        }

        int curPos = 0; // position of the stopword in the title
        ArrayList<Integer> pos = new ArrayList<>(); // -1, size
        pos.add(-1);
        // 2. Remove stop words and stem the words
        for (String word : words) {
            word = word.trim();
//            words[counter] = word;
            if (stopStem.isStopWord(word) || word.equals("")) {
                pos.add(curPos);
            }
//            counter++;
            curPos++;
        }
        //System.out.println("words " + Arrays.toString(words));
        int start = 0;
        int end = 0;
        //System.out.println(pos);
        Hashtable<Integer, Integer> wordId2Freq = new Hashtable<>(); // phrase -> frequency
        pos.add(words.length);
        for (int i = 0; i < pos.size(); i++) {
            end = pos.get(i);
            if (start == end) {
                start = end + 1;
            } else if (end - start > 1) {
                String[] phrase = Arrays.copyOfRange(words, start, end);
                //System.out.println("Phrase: " + Arrays.toString(phrase));
                ArrayList<String> phrases = findAllPhrases(phrase, maxAllowedLength);
                //System.out.println("ArrayList<String> phrases: " + phrases);
                for (String p : phrases) {
                    if (forwardInvertedIndexPhrase.getWordIdTitleFromStem(p) == -1) {
                        forwardInvertedIndexPhrase.addEntry(TreeNames.word2IdTitle.toString(), p, forwardInvertedIndexPhrase.getWordIdTitle());
                        forwardInvertedIndexPhrase.addEntry(TreeNames.IdTitle2Word.toString(), forwardInvertedIndexPhrase.getWordIdTitleFromStem(p), p);
                        //System.out.println(p + " not found in word2IdTitle table. Adding...");
                    }
                    int wordIdTitle = forwardInvertedIndexPhrase.getWordIdTitleFromStem(p);
                    //System.out.println(p + " has wordId: " + wordIdTitle);
                    Integer freq = wordId2Freq.get(wordIdTitle);
                    if (freq != null)
                        wordId2Freq.put(wordIdTitle, freq + 1);
                    else
                        wordId2Freq.put(wordIdTitle, 1);
                }
            }
            start = end + 1;
        }
        // 5. Insert the wordIdTitle and positions into the inverted index
        for (int wordIdTitle : wordId2Freq.keySet()) {
            forwardInvertedIndexPhrase.updateInvertedIdx(TreeNames.invertedIdxTitle.toString(), wordIdTitle, pageId, wordId2Freq.get(wordIdTitle), new ArrayList<>());
        }
//         6. Insert the wordIdTitle and id into the forward index
        for (int wordIdTitle : wordId2Freq.keySet()) {
            forwardInvertedIndexPhrase.updateForwardIdx(TreeNames.forwardIdxTitle.toString(), pageId, wordIdTitle, wordId2Freq.get(wordIdTitle), new ArrayList<>());
        }
    }

    ArrayList<String> findAllPhrases(String[] phrase, Integer maxAllowedLength) {
        ArrayList<String> phrases = new ArrayList<>();
        int allowed = Math.min(phrase.length, maxAllowedLength);
        if(maxAllowedLength == -1)
            allowed = phrase.length;
        for(int i = 2; i <= allowed; i++){// length allowed
            for (int j = 0; j <= phrase.length - i; j++){ // start position
                StringJoiner sj = new StringJoiner(" ");
                for (int k = j; k < j+i; k++) {
                    sj.add(phrase[k]);
                }
                phrases.add(sj.toString());
            }
        }

        return phrases;
    }

    private void indexBody(String url, int maxAllowedLength) throws Exception{
        //System.out.println("Enter index body");
        int pageId = indexTable.getPageIdFromURL(url);
        //System.out.println("pageId: " + pageId);
        if (pageId == -1) throw new Exception("Page ID not found");


        // 1. Extract words from the body
        String[] words = StringExtractor.extractStrings(false, url);
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toLowerCase();
        }

        if (words.length == 0) return;

        for (int i = 0; i < words.length; i++) {
            words[i] = stopStem.stem(words[i].toLowerCase());
        }

        int curPos = 0; // position of the stopword in the body
        ArrayList<Integer> pos = new ArrayList<>(); // -1, size
        pos.add(-1);
        // 2. Remove stop words and stem the words
        for (String word : words) {
            word = word.trim();
//            words[counter] = word;
            if (stopStem.isStopWord(word) || word.equals("")) {
                pos.add(curPos);
            }
//            counter++;
            curPos++;
        }
        //System.out.println("words " + Arrays.toString(words));
        int start = 0;
        int end = 0;
        //System.out.println(pos);
        Hashtable<Integer, Integer> wordId2Freq = new Hashtable<>(); // phrase -> frequency
        pos.add(words.length);
        for (int i = 0; i < pos.size(); i++) {
            end = pos.get(i);
            if (start == end) {
                start = end + 1;
            } else if (end - start > 1) {
                String[] phrase = Arrays.copyOfRange(words, start, end);
                //System.out.println("Phrase: " + Arrays.toString(phrase));
                ArrayList<String> phrases = findAllPhrases(phrase, maxAllowedLength);
                //System.out.println("ArrayList<String> phrases: " + phrases);
                for (String p : phrases) {
                    if (forwardInvertedIndexPhrase.getWordIdBodyFromStem(p) == -1) {
                        forwardInvertedIndexPhrase.addEntry(TreeNames.word2IdBody.toString(), p, forwardInvertedIndexPhrase.getWordIdBody());
                        forwardInvertedIndexPhrase.addEntry(TreeNames.IdBody2Word.toString(), forwardInvertedIndexPhrase.getWordIdBodyFromStem(p), p);
                        //System.out.println(p + " not found in word2IdBody table. Adding...");
                    }
                    int wordIdBody = forwardInvertedIndexPhrase.getWordIdBodyFromStem(p);
                    //System.out.println(p + " has wordId: " + wordIdBody);
                    Integer freq = wordId2Freq.get(wordIdBody);
                    if (freq != null)
                        wordId2Freq.put(wordIdBody, freq + 1);
                    else
                        wordId2Freq.put(wordIdBody, 1);
                }
            }
            start = end + 1;
        }
        // 5. Insert the wordIdBody and positions into the inverted index
        for (int wordIdBody : wordId2Freq.keySet()) {
            forwardInvertedIndexPhrase.updateInvertedIdx(TreeNames.invertedIdxBody.toString(), wordIdBody, pageId, wordId2Freq.get(wordIdBody), new ArrayList<>());
        }
//         6. Insert the wordIdBody and id into the forward index
        for (int wordIdBody : wordId2Freq.keySet()) {
            forwardInvertedIndexPhrase.updateForwardIdx(TreeNames.forwardIdxBody.toString(), pageId, wordIdBody, wordId2Freq.get(wordIdBody), new ArrayList<>());
        }
    }
}