package hk.ust.comp4321.invertedIndex;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/***
 * The class to manage the database for inverted index.
 * The database contains six hTrees: word2IdTitle, word2IdBody, invertedIdxTitle, invertedIdxBody, forwardIdxTitle, forwardIdxBody, IdTitle2Word, IdBody2Word.
 * word2IdTitle: word to id
 * word2IdBody: word to id
 * invertedIdxTitle: word ID to a HashMap. Each entry: page ID -> positions
 * invertedIdxBody: word ID to a HashMap. Each entry: page ID -> positions
 * forwardIdxTitle: page ID to a HashMap. Each entry: word ID -> frequency
 * forwardIdxBody: page ID to a HashMap. Each entry: word ID -> frequency
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
     * Add an entry to the word2IdTitle hTree
     * @param key the word
     * @param value the wordId
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the word already exists
     */
    public <K, V> void addEntryToWord2IdTitle(K key, V value) throws IOException {
        if (word2IdTitle.get(key) == null) {
            word2IdTitle.put(key, value);
            wordIdTitle++; // increment the wordId
        } else {
            System.out.println(key + " already exists in word2IdTitle table");
        }
        recordManager.commit();
    }

    /***
     * Add an entry to the word2IdBody hTree
     * @param key the word
     * @param value the wordId
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the word already exists
     */
    public <K, V> void addEntryToWord2IdBody(K key, V value) throws IOException {
        if (word2IdBody.get(key) == null) {
            word2IdBody.put(key, value);
            wordIdBody++;
        } else {
            System.out.println(key + " already exists in word2IdBody table");
        }
        recordManager.commit();
    }

    /***
     * Add an entry to the IdTitle2Word hTree
     * @param key the wordId
     * @param value the word
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the wordId already exists
     */
    public <K, V> void addEntryToIdTitle2Word(K key, V value) throws IOException {
        if (IdTitle2Word.get(key) == null) {
            IdTitle2Word.put(key, value);
        } else {
            System.out.println(key + " already exists in IdTitle2Word table");
        }
        recordManager.commit();
    }

    /***
     * Add an entry to the IdBody2Word hTree
     * @param key the wordId
     * @param value the word
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the wordId already exists
     */
    public <K, V> void addEntryToIdBody2Word(K key, V value) throws IOException {
        if (IdBody2Word.get(key) == null) {
            IdBody2Word.put(key, value);
        } else {
            System.out.println(key + " already exists in IdBody2Word table");
        }
        recordManager.commit();
    }

    /***
     * Add an entry to the invertedIdx hTree
     * @param wordId the wordId
     * @param pageId the pageId
     * @param pos the position of the word
     * @throws IOException if the wordId is not found
     */
    public void updateInvertedIdxTitle(int wordId, int pageId, ArrayList<Integer> pos) throws IOException { // wordId -> (pageId, pos)
        if (invertedIdxTitle.get(wordId) == null) {
            invertedIdxTitle.put(wordId, new HashMap<>());
        }
        HashMap<Integer, ArrayList<Integer>> pageIdFreqPosMap = (HashMap<Integer, ArrayList<Integer>>) invertedIdxTitle.get(wordId);
        pageIdFreqPosMap.put(pageId, pos);
        invertedIdxTitle.put(wordId, pageIdFreqPosMap);
        recordManager.commit();
    }

    /***
     * Add an entry to the invertedIdx hTree
     * @param wordId the wordId
     * @param pageId the pageId
     * @param pos the position of the word
     * @throws IOException if the wordId is not found
     */
    public void updateInvertedIdxBody(int wordId, int pageId, ArrayList<Integer> pos) throws IOException { // wordId -> (pageId, pos)
        if (invertedIdxBody.get(wordId) == null) {
            invertedIdxBody.put(wordId, new HashMap<>());
        }
        HashMap<Integer, ArrayList<Integer>> pageIdFreqPosMap = (HashMap<Integer, ArrayList<Integer>>) invertedIdxBody.get(wordId);
        pageIdFreqPosMap.put(pageId, pos);
        invertedIdxBody.put(wordId, pageIdFreqPosMap);
        recordManager.commit();
    }

    /***
     * Add an entry to the forwardIdx hTree
     * @param pageID the pageId
     * @param wordId the wordId
     * @param freq the frequency of the word
     * @throws IOException if the pageId is not found
     */
    public void updateForwardIdxTitle(int pageID, int wordId, int freq) throws IOException {
        if (forwardIdxTitle.get(pageID) == null) {
            forwardIdxTitle.put(pageID, new HashMap<>());
        }
        HashMap<Integer, Integer> wordIdFreqMap = (HashMap<Integer, Integer>) forwardIdxTitle.get(pageID);
        wordIdFreqMap.put(wordId, freq);
        forwardIdxTitle.put(pageID, wordIdFreqMap);
        recordManager.commit();
    }

    /***
     * Add an entry to the forwardIdx hTree
     * @param pageID the pageId
     * @param wordId the wordId
     * @param freq the frequency of the word
     * @throws IOException if the pageId is not found
     */
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
     * Get the pageId and positions of the word in the title
     * @param wordID the wordId
     * @return the pageId and positions of the word
     * @throws IOException if the wordId is not found
     */
    public HashMap<Integer, ArrayList<Integer>> getPageIDPosFromWordIdTitle(int wordID) throws IOException {
        if (invertedIdxTitle.get(wordID) == null) {
            return null;
        }
        return (HashMap<Integer, ArrayList<Integer>>) invertedIdxTitle.get(wordID);
    }

    /***
     * Get the pageId and positions of the word in the body
     * @param wordID the wordId
     * @return the pageId and positions of the word
     * @throws IOException if the wordId is not found
     */
    public HashMap<Integer, ArrayList<Integer>> getPageIDPosFromWordIdBody(int wordID) throws IOException {
        if (invertedIdxBody.get(wordID) == null) {
            return null;
        }
        return (HashMap<Integer, ArrayList<Integer>>) invertedIdxBody.get(wordID);
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

    /***
     * Check the inverted index
     */
    public void checkInvertedIdx() {
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
