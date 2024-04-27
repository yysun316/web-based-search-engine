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

public class ForwardInvertedIndex {
    private RecordManager recordManager;
    // for Indexer:
    private HTree word2IdTitle; // word to id {using the word to find the wordId}
    private HTree word2IdBody; // word to id {using the word to find the wordId}
    private HTree invertedIdxTitle; // wordID to list of {id} (tell which webpages contain the word)
    private HTree invertedIdxBody; // wordID to list of {id} for that word in body
    private HTree forwardIdxTitle; // id to wordID list for that page {using the id to find the wordId, sort the wordId by frequency}
    private HTree forwardIdxBody; // id to wordID list for that page {using the id to find the wordId}
    private int wordIdTitle; // increase by 1 for each new word
    private int wordIdBody; // increase by 1 for each new word
    private HTree IdTitle2Word; // wordId to word {using the wordId to find the word}
    private HTree IdBody2Word; // wordId to word {using the wordId to find the word}

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

    public static int getSize(HTree tree) throws IOException {
        FastIterator iter = tree.keys();
        Object key;
        int count = 0;
        while ((key = iter.next()) != null) {
            count++;
        }
        return count;
    }

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

    public void updateInvertedIdx(String hTreeName, int wordId, int pageId, int freq, ArrayList<Integer> pos) throws IOException {
        // Posting: pageId, freq
        // wordId -> (posting, pos)
        Posting post = new Posting(pageId, freq);
        switch (hTreeName) {
            case "invertedIdxTitle" -> {
                // if this wordIdTitle is not in the invertedIdxTitle, create a new BTree
                BTree list;
                if (invertedIdxTitle.get(wordId) == null) { // it stores the record.
                    list = BTree.createInstance(recordManager, new Posting.IdComparator()); // sort by pageId
                    recordManager.setNamedObject(((Integer) wordId) + "InvertedIdxTitle", list.getRecid()); // name of list is wordId
                    invertedIdxTitle.put(wordId, list.getRecid()); // add the name of the list to the invertedIdxTitle
                }
                list = BTree.load(recordManager, (Long) invertedIdxTitle.get(wordId)); // load the list
                // if the pageId is already in the list (when page has modification)
                list.insert(post, pos, true);
            }
            case "invertedIdxBody" -> {
                // if this wordIdBody is not in the invertedIdxBody, create a new BTree
                BTree list;
                if (invertedIdxBody.get(wordId) == null) {
                    // wordIdBody, {((id, pos), frequency), (...), ...}
                    list = BTree.createInstance(recordManager, new Posting.IdComparator());
                    recordManager.setNamedObject(((Integer) wordId) + "InvertedIdxBody", list.getRecid());
                    invertedIdxBody.put(wordId, list.getRecid());
                }
                list = BTree.load(recordManager, (Long) invertedIdxBody.get(wordId));
                list.insert(post, pos, true);
            }
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    public void updateForwardIdx(String hTreeName, int pageID, int wordId, int freq, ArrayList<Integer> pos) throws IOException {
        // pageId -> (freq, wordId) (sort by term frequency) but not working because we cannot find the wordId for removal
        // pageId -> (posting: (wordId, freq), pos) can be used to find the wordId for removal and can sort by frequency
        Posting post = new Posting(wordId, freq);
        switch (hTreeName) {
            case "forwardIdxTitle" -> {
                // if this id is not in the forwardIdxTitle, create a new BTree
                BTree list;
                if (forwardIdxTitle.get(pageID) == null) {
                    list = BTree.createInstance(recordManager, new Posting.FreqComparator()); // sort by term frequency
                    recordManager.setNamedObject(((Integer) pageID) + "ForwardIdxTitle", list.getRecid());
                    forwardIdxTitle.put(pageID, list.getRecid());
                }
                list = BTree.load(recordManager, (Long) forwardIdxTitle.get(pageID));
                // remove the old entry with same wordId
                list.insert(post, pos, true); // insert the wordId and pos
            }
            case "forwardIdxBody" -> {
                // if this id is not in the forwardIdxBody, create a new BTree
                BTree list;
                if (forwardIdxBody.get(pageID) == null) {
                    list = BTree.createInstance(recordManager, new Posting.FreqComparator()); // sort by term frequency
                    recordManager.setNamedObject(((Integer) pageID) + "ForwardIdxBody", list.getRecid());
                    forwardIdxBody.put(pageID, list.getRecid());
                }
                list = BTree.load(recordManager, (Long) forwardIdxBody.get(pageID));
                list.insert(post, pos, true);
            }
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    public int getWordIdTitleFromStem(String stem) throws IOException {
        Object wordId = word2IdTitle.get(stem);
        //recordManager.commit();
        return wordId != null ? (int) wordId : -1;
    }

    public int getWordIdTitle() {
        return wordIdTitle;
    }

    public int getWordIdBodyFromStem(String stem) throws IOException {
        Object wordId = word2IdBody.get(stem);
        return wordId != null ? (int) wordId : -1;
    }

    public int getWordIdBody() {
        return wordIdBody;
    }

    //tb=2 : want body only = do not want title
    //tb=1 : want title only = do not want body
    public Map<String, Integer> getKeywordFrequency(int pageId, int numKeywords, int tb) throws IOException {
        Hashtable<String, Integer> keywordFrequency = new Hashtable<>();
        long recid = recordManager.getNamedObject(((Integer) pageId) + "ForwardIdxTitle");
        if (recid != 0 && forwardIdxTitle.get(pageId) != null && tb!=2){
            BTree list = BTree.load(recordManager, (Long) forwardIdxTitle.get(pageId));
            TupleBrowser browser = list.browse();
            Tuple tuple = new Tuple();
            while (browser.getNext(tuple)) {
                Posting post = (Posting) tuple.getKey();
                String keyword = (String) IdTitle2Word.get(post.getId());
                if (keywordFrequency.containsKey(keyword))
                    keywordFrequency.put(keyword, keywordFrequency.get(keyword) + post.getFreq());
                else
                    keywordFrequency.put(keyword, post.getFreq());
            }

        }
        long recid2 = recordManager.getNamedObject(((Integer) pageId) + "ForwardIdxBody");
        if (recid2 != 0 && forwardIdxBody.get(pageId) != null && tb!=1){
            BTree list2 = BTree.load(recordManager, (Long) forwardIdxBody.get(pageId));
            TupleBrowser browser2 = list2.browse();
            Tuple tuple2 = new Tuple();
            while (browser2.getNext(tuple2)) {
                Posting post = (Posting) tuple2.getKey();
                String keyword = (String) IdBody2Word.get(post.getId());
                if (keywordFrequency.containsKey(keyword))
                    keywordFrequency.put(keyword, keywordFrequency.get(keyword) + post.getFreq());
                else
                    keywordFrequency.put(keyword, post.getFreq());
            }
        }


        // Convert the Hashtable into a List of entries
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(keywordFrequency.entrySet());

        // Sort the entries by their values (frequencies)
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Convert the sorted entries back into a LinkedHashMap
        if (numKeywords == -1)
            return entries.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
        return entries.stream()
                .limit(numKeywords)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    public String getWordFromIdBody(int wordId) throws IOException {
        return (String) IdBody2Word.get(wordId);
    }
    public String getWordFromIdTitle(int wordId) throws IOException {
        return (String) IdTitle2Word.get(wordId);
    }
    // for indexer phrases
    public BTree getTreeBodyFromWordId(int wordID) throws IOException{
        BTree list;
        if (invertedIdxBody.get(wordID) == null) {
            return null;
        }
        list = BTree.load(recordManager, (Long) invertedIdxBody.get(wordID));
        return list;
    }
    public BTree getTreeTitleFromWordId(int wordID) throws IOException{
        BTree list;
        if (invertedIdxTitle.get(wordID) == null) {
            return null;
        }
        list = BTree.load(recordManager, (Long) invertedIdxTitle.get(wordID));
        return list;
        //return (BTree) invertedIdxTitle.get(wordID);
    }
}
