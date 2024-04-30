package hk.ust.comp4321.invertedIndex;

import hk.ust.comp4321.utils.Posting;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.FastIterator;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/***
 * The class to manage the database for inverted index.
 * The database contains six hTrees: word2IdTitle, word2IdBody, invertedIdxTitle, invertedIdxBody, forwardIdxTitle, forwardIdxBody, IdTitle2Word, IdBody2Word.
 * word2IdTitle: word to id
 * word2IdBody: word to id
 * invertedIdxTitle: word ID to a BTree. Each entry: (page ID, freq) -> positions
 * invertedIdxBody: word ID to a BTree. Each entry: (page ID, freq) -> positions
 * forwardIdxTitle: page ID to a BTree. Each entry (wordId, freq) -> positions
 * forwardIdxBody: page ID to a BTree. Each entry (wordId, freq) -> position
 * IdTitle2Word: wordId to word
 * IdBody2Word: wordId to word
 */
public class ForwardInvertedIndex {
    private RecordManager recordManager;
    private HTree word2IdTitle;
    private HTree word2IdBody;
    private HTree invertedIdxTitle;
    private HTree invertedIdxBody;
    private HTree forwardIdxTitle;
    private HTree forwardIdxBody;
    private int wordIdTitle;
    private int wordIdBody;
    private HTree IdTitle2Word;
    private HTree IdBody2Word;

    /***
     * Constructor of the class
     * @param recordManagerName the name of the record manager
     * @throws IOException if the record manager is not found
     */
    public ForwardInvertedIndex(String recordManagerName) throws IOException {
        this.recordManager = RecordManagerFactory.createRecordManager(recordManagerName);

        long recid3 = recordManager.getNamedObject("word2IdTitle");
        long recid4 = recordManager.getNamedObject("word2IdBody");
        long recid5 = recordManager.getNamedObject("invertedIdxTitle");
        long recid6 = recordManager.getNamedObject("invertedIdxBody");
        long recid7 = recordManager.getNamedObject("forwardIdxTitle");
        long recid8 = recordManager.getNamedObject("forwardIdxBody");
        long recid9 = recordManager.getNamedObject("idTitle2Word");
        long recid10 = recordManager.getNamedObject("idBody2Word");

        if (recid3 != 0) {
            word2IdTitle = HTree.load(recordManager, recid3);
        } else {
            word2IdTitle = HTree.createInstance(recordManager);
            recordManager.setNamedObject("word2IdTitle", word2IdTitle.getRecid());
        }
        if (recid4 != 0) {
            word2IdBody = HTree.load(recordManager, recid4);
        } else {
            word2IdBody = HTree.createInstance(recordManager);
            recordManager.setNamedObject("word2IdBody", word2IdBody.getRecid());
        }
        if (recid5 != 0) {
            invertedIdxTitle = HTree.load(recordManager, recid5);
        } else {
            invertedIdxTitle = HTree.createInstance(recordManager);
            recordManager.setNamedObject("invertedIdxTitle", invertedIdxTitle.getRecid());
        }
        if (recid6 != 0) {
            invertedIdxBody = HTree.load(recordManager, recid6);
        } else {
            invertedIdxBody = HTree.createInstance(recordManager);
            recordManager.setNamedObject("invertedIdxBody", invertedIdxBody.getRecid());
        }
        if (recid7 != 0) {
            forwardIdxTitle = HTree.load(recordManager, recid7);
        } else {
            forwardIdxTitle = HTree.createInstance(recordManager);
            recordManager.setNamedObject("forwardIdxTitle", forwardIdxTitle.getRecid());
        }
        if (recid8 != 0) {
            forwardIdxBody = HTree.load(recordManager, recid8);
        } else {
            forwardIdxBody = HTree.createInstance(recordManager);
            recordManager.setNamedObject("forwardIdxBody", forwardIdxBody.getRecid());
        }
        if (recid9 != 0) {
            IdTitle2Word = HTree.load(recordManager, recid9);
        } else {
            IdTitle2Word = HTree.createInstance(recordManager);
            recordManager.setNamedObject("idTitle2Word", IdTitle2Word.getRecid());
        }
        if (recid10 != 0) {
            IdBody2Word = HTree.load(recordManager, recid10);
        } else {
            IdBody2Word = HTree.createInstance(recordManager);
            recordManager.setNamedObject("idBody2Word", IdBody2Word.getRecid());
        }
        wordIdTitle = getSize(word2IdTitle);
        wordIdBody = getSize(word2IdBody);
    }

    /***
     * Get the size of the hTree
     * @param tree the hTree
     * @return the size of the hTree
     * @throws IOException if the hTree is not found
     */
    public static int getSize(HTree tree) throws IOException {
        FastIterator iter = tree.keys();
        Object key;
        int count = 0;
        while ((key = iter.next()) != null) {
            count++;
        }
        return count;
    }

    /***
     * Add an entry to the hTree (excluding invertedIdx and forwardIdx)
     * @param hTreeName the name of the hTree
     * @param key the key of the value
     * @param value the value of the key
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the tree name is invalid
     */

    public <K, V> void addEntry(String hTreeName, K key, V value) throws IOException {
        switch (hTreeName) {
            case "word2IdTitle" -> {
                if (word2IdTitle.get(key) == null) {
                    word2IdTitle.put(key, value);
                    wordIdTitle++;
                } else {
                    System.out.println(key + " already exists in word2IdTitle table");
                }
            }
            case "word2IdBody" -> {
                if (word2IdBody.get(key) == null) {
                    word2IdBody.put(key, value);
                    wordIdBody++;
                } else {
                    System.out.println(key + " already exists in word2IdBody table");
                }
            }
            case "IdTitle2Word" -> {
                if (IdTitle2Word.get(key) == null) {
                    IdTitle2Word.put(key, value);
                } else {
                    System.out.println(key + " already exists in IdTitle2Word table");
                }
            }
            case "IdBody2Word" -> {
                if (IdBody2Word.get(key) == null) {
                    IdBody2Word.put(key, value);
                } else {
                    System.out.println(key + " already exists in IdBody2Word table");
                }
            }
            default -> throw new IllegalArgumentException("Invalid hTreeName: " + hTreeName);
        }
        recordManager.commit();
    }

    public <K, V> void addEntryToWord2IdTitle(K key, V value) throws IOException {
        if (word2IdTitle.get(key) == null) {
            word2IdTitle.put(key, value);
            wordIdTitle++;
        } else {
            System.out.println(key + " already exists in word2IdTitle table");
        }
        recordManager.commit();
    }

    public <K, V> void addEntryToWord2IdBody(K key, V value) throws IOException {
        if (word2IdBody.get(key) == null) {
            word2IdBody.put(key, value);
            wordIdBody++;
        } else {
            System.out.println(key + " already exists in word2IdBody table");
        }
        recordManager.commit();
    }

    public <K, V> void addEntryToIdTitle2Word(K key, V value) throws IOException {
        if (IdTitle2Word.get(key) == null) {
            IdTitle2Word.put(key, value);
        } else {
            System.out.println(key + " already exists in IdTitle2Word table");
        }
        recordManager.commit();
    }

    public <K, V> void addEntryToIdBody2Word(K key, V value) throws IOException {
        if (IdBody2Word.get(key) == null) {
            IdBody2Word.put(key, value);
        } else {
            System.out.println(key + " already exists in IdBody2Word table");
        }
        recordManager.commit();
    }



//    public void updateInvertedIdx(String hTreeName, int wordId, int pageId, int freq, ArrayList<Integer> pos) throws IOException {
//        // Posting: pageId, freq
//        // wordId -> (posting, pos)
//        Posting post = new Posting(pageId, freq);
//        switch (hTreeName) {
//            case "invertedIdxTitle" -> {
//                // if this wordIdTitle is not in the invertedIdxTitle, create a new BTree
//                BTree list;
//                if (invertedIdxTitle.get(wordId) == null) { // it stores the record.
//                    list = BTree.createInstance(recordManager, new Posting.IdComparator()); // sort by pageId
//                    recordManager.setNamedObject(((Integer) wordId) + "InvertedIdxTitle", list.getRecid()); // name of list is wordId
//                    invertedIdxTitle.put(wordId, list.getRecid()); // add the name of the list to the invertedIdxTitle
//                }
//                list = BTree.load(recordManager, (Long) invertedIdxTitle.get(wordId)); // load the list
//                // if the pageId is already in the list (when page has modification)
//                list.insert(post, pos, true);
//            }
//            case "invertedIdxBody" -> {
//                // if this wordIdBody is not in the invertedIdxBody, create a new BTree
//                BTree list;
//                if (invertedIdxBody.get(wordId) == null) {
//                    // wordIdBody, {((id, pos), frequency), (...), ...}
//                    list = BTree.createInstance(recordManager, new Posting.IdComparator());
//                    recordManager.setNamedObject(((Integer) wordId) + "InvertedIdxBody", list.getRecid());
//                    invertedIdxBody.put(wordId, list.getRecid());
//                }
//                list = BTree.load(recordManager, (Long) invertedIdxBody.get(wordId));
//                list.insert(post, pos, true);
//            }
//            default -> throw new IllegalArgumentException("Invalid hTreeName");
//        }
//        recordManager.commit();
//    }

//    public void updateInvertedIdxTitle(int wordId, int pageId, int freq, ArrayList<Integer> pos) throws IOException {
//        Posting post = new Posting(pageId, freq);
//        BTree list;
//        if (invertedIdxTitle.get(wordId) == null) {
//            list = BTree.createInstance(recordManager, new Posting.IdComparator());
//            recordManager.setNamedObject(((Integer) wordId) + "InvertedIdxTitle", list.getRecid());
//            invertedIdxTitle.put(wordId, list.getRecid());
//        }
//        list = BTree.load(recordManager, (Long) invertedIdxTitle.get(wordId));
//        list.insert(post, pos, true);
//        recordManager.commit();
//    }
//
//    public void updateInvertedIdxBody(int wordId, int pageId, int freq, ArrayList<Integer> pos) throws IOException {
//        Posting post = new Posting(pageId, freq);
//        BTree list;
//        if (invertedIdxBody.get(wordId) == null) {
//            list = BTree.createInstance(recordManager, new Posting.IdComparator());
//            recordManager.setNamedObject(((Integer) wordId) + "InvertedIdxBody", list.getRecid());
//            invertedIdxBody.put(wordId, list.getRecid());
//        }
//        list = BTree.load(recordManager, (Long) invertedIdxBody.get(wordId));
//        list.insert(post, pos, true);
//        recordManager.commit();
//    }
//
//    public void updateInvertedIdxTitle(int wordId, int pageId, int freq, ArrayList<Integer> pos) throws IOException {
//        Posting post = new Posting(pageId, freq);
//        if (invertedIdxTitle.get(wordId) == null) {
//            invertedIdxTitle.put(wordId, new TreeMap<>(new Posting.IdComparator()));
//        }
//        TreeMap<Posting, ArrayList<Integer>> postingsList = (TreeMap<Posting, ArrayList<Integer>>) invertedIdxTitle.get(wordId);
//        postingsList.put(post, pos);
//        recordManager.commit();
//    }
//
//    public void updateInvertedIdxBody(int wordId, int pageId, int freq, ArrayList<Integer> pos) throws IOException {
//        Posting post = new Posting(pageId, freq);
//        if (invertedIdxBody.get(wordId) == null) {
//            invertedIdxBody.put(wordId, new TreeMap<>(new Posting.IdComparator()));
//        }
//        TreeMap<Posting, ArrayList<Integer>> postingsList = (TreeMap<Posting, ArrayList<Integer>>) invertedIdxBody.get(wordId);
//        postingsList.put(post, pos);
//        recordManager.commit();
//    }

    public void updateInvertedIdxTitle(int wordId, int pageId, ArrayList<Integer> pos) throws IOException { // wordId -> (pageId, pos)
        // Get the map of pageId to frequency and positions for the given wordId
        if (invertedIdxTitle.get(wordId) == null) {
            invertedIdxTitle.put(wordId, new HashMap<>());
        }
        HashMap<Integer, ArrayList<Integer>> pageIdFreqPosMap = (HashMap<Integer, ArrayList<Integer>>) invertedIdxTitle.get(wordId);

        // Update the frequency and positions for the pageId
        pageIdFreqPosMap.put(pageId, pos);

        // Update the invertedIdxTitle with the new pageIdFreqPosMap
        invertedIdxTitle.put(wordId, pageIdFreqPosMap);

        recordManager.commit();
    }

    public void updateInvertedIdxBody(int wordId, int pageId, ArrayList<Integer> pos) throws IOException { // wordId -> (pageId, pos)
        // Get the map of pageId to frequency and positions for the given wordId
        if (invertedIdxBody.get(wordId) == null) {
            invertedIdxBody.put(wordId, new HashMap<>());
        }
        HashMap<Integer, ArrayList<Integer>> pageIdFreqPosMap = (HashMap<Integer, ArrayList<Integer>>) invertedIdxBody.get(wordId);

        // Update the frequency and positions for the pageId
        pageIdFreqPosMap.put(pageId, pos);

        // Update the invertedIdxBody with the new pageIdFreqPosMap
        invertedIdxBody.put(wordId, pageIdFreqPosMap);

        recordManager.commit();
    }



    public void updateForwardIdxTitle(int pageID, int wordId, int freq) throws IOException {
        // Get the map of wordId to frequency for the given pageID
        if (forwardIdxTitle.get(pageID) == null) {
            forwardIdxTitle.put(pageID, new HashMap<>());
        }
        HashMap<Integer, Integer> wordIdFreqMap = (HashMap<Integer, Integer>) forwardIdxTitle.get(pageID);

        // Update the frequency for the wordId
        wordIdFreqMap.put(wordId, freq);

        // Update the forwardIdxTitle with the new wordIdFreqMap
        forwardIdxTitle.put(pageID, wordIdFreqMap);

        recordManager.commit();
    }

    public void updateForwardIdxBody(int pageID, int wordId, int freq) throws IOException {
        // Get the map of wordId to frequency for the given pageID
        if (forwardIdxBody.get(pageID) == null) {
            forwardIdxBody.put(pageID, new HashMap<>());
        }
        HashMap<Integer, Integer> wordIdFreqMap = (HashMap<Integer, Integer>) forwardIdxBody.get(pageID);

        // Update the frequency for the wordId
        wordIdFreqMap.put(wordId, freq);

        // Update the forwardIdxBody with the new wordIdFreqMap
        forwardIdxBody.put(pageID, wordIdFreqMap);

        recordManager.commit();
    }

    /***
     * Remove an entry in the hTree
     * @param stem the word
     * @return the wordId
     * @throws IOException if the word is not found
     */
    public int getWordIdTitleFromStem(String stem) throws IOException {
        Object wordId = word2IdTitle.get(stem);
        return wordId != null ? (int) wordId : -1;
    }

    /***
     * Get the wordId of the title
     * @return the wordId of the title
     */
    public int getWordIdTitle() {
        return wordIdTitle;
    }

    /***
     * Get the wordId of the body
     * @param stem the word
     * @return the wordId
     * @throws IOException if the word is not found
     */
    public int getWordIdBodyFromStem(String stem) throws IOException {
        Object wordId = word2IdBody.get(stem);
        return wordId != null ? (int) wordId : -1;
    }

    /***
     * Get the wordId of the body
     * @return the wordId of the body
     */

    public int getWordIdBody() {
        return wordIdBody;
    }

    /***
     * Get the frequency of the keywords in the page
     * @param pageId the pageId
     * @param numKeywords the number of keywords
     * @param tb the type of the body
     *       tb=2 : want body only = do not want title
     *       tb=1 : want title only = do not want body
     * @return the frequency of the keywords
     * @throws IOException if the pageId is not found
     */
    public Map<String, Integer> getKeywordFrequency(int pageId, int numKeywords, int tb) throws IOException {
        Hashtable<String, Integer> keywordFrequency = new Hashtable<>();
        if (forwardIdxTitle.get(pageId) != null && tb != 2) {
            HashMap<Integer, Integer> wordIdFreqMap = (HashMap<Integer, Integer>) forwardIdxTitle.get(pageId);
            for (Map.Entry<Integer, Integer> entry : wordIdFreqMap.entrySet()) {
                String keyword = (String) IdTitle2Word.get(entry.getKey());
                keywordFrequency.put(keyword, entry.getValue());
            }
        }
        if (forwardIdxBody.get(pageId) != null && tb != 1) {
            HashMap<Integer, Integer> wordIdFreqMap = (HashMap<Integer, Integer>) forwardIdxBody.get(pageId);
            for (Map.Entry<Integer, Integer> entry : wordIdFreqMap.entrySet()) {
                String keyword = (String) IdBody2Word.get(entry.getKey());
                keywordFrequency.put(keyword, keywordFrequency.getOrDefault(keyword, 0) + entry.getValue());
            }
        }

        // Convert the Hashtable into a List of entries
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(keywordFrequency.entrySet());

        // Sort the entries by their values (frequencies)
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Convert the sorted entries back into a LinkedHashMap
        if (numKeywords == -1)
            return entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
        return entries.stream().limit(numKeywords).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    /***
     * Get the word from the wordId
     * @param wordId the wordId
     * @return the word
     * @throws IOException if the wordId is not found
     */
    public String getWordFromIdBody(int wordId) throws IOException {
        return (String) IdBody2Word.get(wordId);
    }

    /***
     * Get the word from the wordId
     * @param wordId the wordId
     * @return the word
     * @throws IOException if the wordId is not found
     */
    public String getWordFromIdTitle(int wordId) throws IOException {
        return (String) IdTitle2Word.get(wordId);
    }

    /***
     * Get the list of pageId from the wordId
     * @param wordID the wordId
     * @return the list of pageId
     * @throws IOException if the wordId is not found
     */
    public BTree getTreeBodyFromWordId(int wordID) throws IOException {
        BTree list;
        if (invertedIdxBody.get(wordID) == null) {
            return null;
        }
        list = BTree.load(recordManager, (Long) invertedIdxBody.get(wordID));
        return list;
    }

    /***
     * Get the list of pageId from the wordId
     * @param wordID the wordId
     * @return the list of pageId
     * @throws IOException if the wordId is not found
     */
    public BTree getTreeTitleFromWordId(int wordID) throws IOException { // TODO: change
        BTree list;
        if (invertedIdxTitle.get(wordID) == null) {
            return null;
        }
        list = BTree.load(recordManager, (Long) invertedIdxTitle.get(wordID));
        return list;
    }

    /***
     * Get invertedIdxTitle hTree
     * @return invertedIdxTitle
     */
    public HTree getInvertedIdxTitle() {
        return invertedIdxTitle;
    }

    /***
     * Get invertedIdxBody hTree
     * @return invertedIdxBody
     */
    public HTree getInvertedIdxBody() {
        return invertedIdxBody;
    }

    /***
     * Get forwardIdxTitle hTree
     * @return forwardIdxTitle
     */
    public HTree getForwardIdxTitle() {
        return forwardIdxTitle;
    }

    /***
     * Get forwardIdxBody hTree
     * @return forwardIdxBody
     */
    public HTree getForwardIdxBody() {
        return forwardIdxBody;
    }

    /***
     * Get word2IdTitle hTree
     * @return word2IdTitle
     */
    public HTree getWord2IdTitle() {
        return word2IdTitle;
    }

    /***
     * Get word2IdBody hTree
     * @return word2IdBody
     */
    public HTree getWord2IdBody() {
        return word2IdBody;
    }

    /***
     * Get IdTitle2Word hTree
     * @return IdTitle2Word
     */
    public HTree getIdTitle2Word() {
        return IdTitle2Word;
    }

    /***
     * Get IdBody2Word hTree
     * @return IdBody2Word
     */
    public HTree getIdBody2Word() {
        return IdBody2Word;
    }

    /***
     * Close the record manager
     * @throws IOException if the record manager is not found
     */
    public void close() throws IOException {
        recordManager.close();
    }

    public void checkInvertedIdx() throws IOException {
        System.out.println("Checking the inverted index for title...");
        HTree title = getInvertedIdxTitle();
        FastIterator titleBrowser = title.keys();
        Object key;
        while ((key = titleBrowser.next()) != null) {
            int wordId = (int) key;
            System.out.println("word: " + getWordFromIdTitle(wordId));
            BTree list = BTree.load(recordManager, (Long) invertedIdxTitle.get(wordId));
            TupleBrowser browser = list.browse();
            Tuple tuple = new Tuple();
            while (browser.getNext(tuple)) {
                Posting post = (Posting) tuple.getKey();
                System.out.print("pageId: " + post.getId() + ", freq: " + post.getFreq() + ", ");
            }
            System.out.println();
        }

        System.out.println("Checking the inverted index for body...");
        HTree body = getInvertedIdxBody();
        FastIterator bodyBrowser = body.keys();
        while ((key = bodyBrowser.next()) != null) {
            int wordId = (int) key;
            System.out.println("word: " + getWordFromIdBody(wordId));
            BTree list = BTree.load(recordManager, (Long) invertedIdxBody.get(wordId));
            TupleBrowser browser = list.browse();
            Tuple tuple = new Tuple();
            while (browser.getNext(tuple)) {
                Posting post = (Posting) tuple.getKey();
                System.out.print("pageId: " + post.getId() + ", freq: " + post.getFreq() + ", ");
            }
            System.out.println();
        }
    }

    public void checkInvertedIdx2() {
        try {
            System.out.println("Checking the inverted index for title...");
            HTree title = getInvertedIdxTitle();
            FastIterator titleBrowser = title.keys();
            Object key;
            while ((key = titleBrowser.next()) != null) {
                int wordId = (int) key;
                System.out.print("word: " + getWordFromIdTitle(wordId));
                HashMap<Integer, ArrayList<Integer>> pageIdFreqPosMap = (HashMap<Integer, ArrayList<Integer>>) invertedIdxTitle.get(wordId);
                for (Map.Entry<Integer, ArrayList<Integer>> entry : pageIdFreqPosMap.entrySet()) {
                    int pageId = entry.getKey();
                    int freq = entry.getValue().size();
                    System.out.print("pageId: " + pageId + ", freq: " + freq + ", ");
                }
                System.out.println();
            }

            System.out.println("Checking the inverted index for body...");
            HTree body = getInvertedIdxBody();
            FastIterator bodyBrowser = body.keys();
            while ((key = bodyBrowser.next()) != null) {
                int wordId = (int) key;
                System.out.print("word: " + getWordFromIdBody(wordId));
                HashMap<Integer, ArrayList<Integer>> pageIdFreqPosMap = (HashMap<Integer, ArrayList<Integer>>) invertedIdxBody.get(wordId);
                for (Map.Entry<Integer, ArrayList<Integer>> entry : pageIdFreqPosMap.entrySet()) {
                    int pageId = entry.getKey();
                    int freq = entry.getValue().size();
                    System.out.print("pageId: " + pageId + ", freq: " + freq + ", ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
