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
import java.util.ArrayList;


public class IndexTable {
    private RecordManager recordManager;
    // for Crawler:
    private HTree url2Id; // url to id {using the url to find the id}
    private HTree id2WebNode; // id to webNode object {using the id to find the webNode}
    // for Indexer:
    private HTree word2IdTitle; // word to id {using the word to find the wordId}
    private HTree word2IdBody; // word to id {using the word to find the wordId}
    private HTree invertedIdxTitle; // wordID to list of {id} (tell which webpages contain the word)
    private HTree invertedIdxBody; // wordID to list of {id} for that word in body
    private HTree forwardIdxTitle; // id to wordID list for that page {using the id to find the wordId, sort the wordId by frequency}
    private HTree forwardIdxBody; // id to wordID list for that page {using the id to find the wordId}
    private int pageId; // increase by 1 for each new page (url2ID add entry)
    private int wordIdTitle; // increase by 1 for each new word
    private int wordIdBody; // increase by 1 for each new word
    private HTree IdTitle2Word; // wordId to word {using the wordId to find the word}
    private HTree IdBody2Word; // wordId to word {using the wordId to find the word}

    public IndexTable(String recordManagerName) throws IOException {
        recordManager = RecordManagerFactory.createRecordManager(recordManagerName);

        long recId1 = recordManager.getNamedObject("url2Id");
        long recId2 = recordManager.getNamedObject("id2WebNode");
        long recId3 = recordManager.getNamedObject("word2IdTitle");
        long recId4 = recordManager.getNamedObject("word2IdBody");
        long recId5 = recordManager.getNamedObject("invertedIdxTitle");
        long recId6 = recordManager.getNamedObject("invertedIdxBody");
        long recId7 = recordManager.getNamedObject("forwardIdxTitle");
        long recId8 = recordManager.getNamedObject("forwardIdxBody");
        long recId9 = recordManager.getNamedObject("IdTitle2Word");
        long recId10 = recordManager.getNamedObject("IdBody2Word");

        if (recId1 != 0) this.url2Id = HTree.load(recordManager, recId1);
        else {
            this.url2Id = HTree.createInstance(recordManager);
            recordManager.setNamedObject("url2Id", this.url2Id.getRecid());
        }

        if (recId2 != 0) this.id2WebNode = HTree.load(recordManager, recId2);
        else {
            this.id2WebNode = HTree.createInstance(recordManager);
            recordManager.setNamedObject("id2WebNode", this.id2WebNode.getRecid());
        }

        if (recId3 != 0) this.word2IdTitle = HTree.load(recordManager, recId3);
        else {
            this.word2IdTitle = HTree.createInstance(recordManager);
            recordManager.setNamedObject("word2IdTitle", this.word2IdTitle.getRecid());
        }

        if (recId4 != 0) this.word2IdBody = HTree.load(recordManager, recId4);
        else {
            this.word2IdBody = HTree.createInstance(recordManager);
            recordManager.setNamedObject("word2IdBody", this.word2IdBody.getRecid());
        }

        if (recId5 != 0) this.invertedIdxTitle = HTree.load(recordManager, recId5);
        else {
            this.invertedIdxTitle = HTree.createInstance(recordManager);
            recordManager.setNamedObject("invertedIdxTitle", this.invertedIdxTitle.getRecid());
        }

        if (recId6 != 0) this.invertedIdxBody = HTree.load(recordManager, recId6);
        else {
            this.invertedIdxBody = HTree.createInstance(recordManager);
            recordManager.setNamedObject("invertedIdxBody", this.invertedIdxBody.getRecid());
        }

        if (recId7 != 0) this.forwardIdxTitle = HTree.load(recordManager, recId7);
        else {
            this.forwardIdxTitle = HTree.createInstance(recordManager);
            recordManager.setNamedObject("forwardIdxTitle", this.forwardIdxTitle.getRecid());
        }

        if (recId8 != 0) this.forwardIdxBody = HTree.load(recordManager, recId8);
        else {
            this.forwardIdxBody = HTree.createInstance(recordManager);
            recordManager.setNamedObject("forwardIdxBody", this.forwardIdxBody.getRecid());
        }
        if (recId9 != 0) this.IdTitle2Word = HTree.load(recordManager, recId9);
        else {
            this.IdTitle2Word = HTree.createInstance(recordManager);
            recordManager.setNamedObject("IdTitle2Word", this.IdTitle2Word.getRecid());
        }
        if (recId10 != 0) this.IdBody2Word = HTree.load(recordManager, recId10);
        else {
            this.IdBody2Word = HTree.createInstance(recordManager);
            recordManager.setNamedObject("IdBody2Word", this.IdBody2Word.getRecid());
        }
        pageId = getSize(url2Id);
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
     * Close the record manager
     * @throws IOException if the record manager is not closed
     */
    public void close() throws IOException { // change finalize to close
        recordManager.commit();
        recordManager.close();
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
            case "url2Id" -> {
                if (url2Id.get(key) == null) url2Id.put(key, pageId++);
                else System.out.println("The URL already exists in the database.");
            }
            case "id2WebNode" -> {
                if (id2WebNode.get(key) == null) id2WebNode.put(key, value); // non-serializable object
                else System.out.println("The WebNode already exists in the database.");
            }
            case "word2IdTitle" -> {
                if (word2IdTitle.get(key) == null) word2IdTitle.put(key, wordIdTitle++);
                else System.out.println("The word already exists in the title database.");
            }
            case "word2IdBody" -> {
                if (word2IdBody.get(key) == null) word2IdBody.put(key, wordIdBody++);
                else System.out.println("The word already exists in the body database.");
            }
            case "IdTitle2Word" -> {
                if (IdTitle2Word.get(key) == null) IdTitle2Word.put(key, value);
                else System.out.println("The word already exists in the IdTitle2Word database.");
            }
            case "IdBody2Word" -> {
                if (IdBody2Word.get(key) == null) IdBody2Word.put(key, value);
                else System.out.println("The word already exists in the IdBody2Word database.");
            }
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    /***
     * Update the value of the key in the hTree
     * @param hTreeName the name of the hTree
     * @param key the key of the value
     * @param value the value of the key
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the tree name is invalid
     */
    public <K, V> void updateEntry(String hTreeName, K key, V value) throws IOException {
        switch (hTreeName) {
            case "url2Id", "word2IdTitle", "word2IdBody", "IdTitle2Word", "IdBody2Word" ->
                    System.out.println("ID should not be updated.");
            case "id2WebNode" -> {
                if (id2WebNode.get(key) == null)
                    System.out.println("The WebNode does not exist in the database. Please add it first.");
                else id2WebNode.put(key, value); // should update the value only
            }
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    /***
     * Update the inverted index
     * @param hTreeName the name of the hTree
     * @param wordId the wordId
     * @param pageId the pageId
     * @param freq the term frequency of the word
     * @param pos the list of positions of the word
     * @throws IOException if the tree name is invalid
     */
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

    /***
     * Update the forward index
     * @param hTreeName the name of the hTree
     * @param pageID the pageId
     * @param wordId the wordId
     * @param freq the term frequency of the word
     * @throws IOException if the tree name is invalid
     */
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

    // TODO: add delete entry (X)
    public <K> void delEntry(String hTreeName, K key) throws IOException {
        switch (hTreeName) {
            case "url2Id" -> url2Id.remove(key);
            case "id2WebNode" -> id2WebNode.remove(key);
            case "word2IdTitle" -> word2IdTitle.remove(key);
            case "word2IdBody" -> word2IdBody.remove(key);
            case "invertedIdxTitle" -> invertedIdxTitle.remove(key);
            case "invertedIdxBody" -> invertedIdxBody.remove(key);
            case "forwardIdxTitle" -> forwardIdxTitle.remove(key);
            case "forwardIdxBody" -> forwardIdxBody.remove(key);
            case "IdTitle2Word" -> IdTitle2Word.remove(key);
            case "IdBody2Word" -> IdBody2Word.remove(key);
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    // TODO: add update entry (X)
    public void printAll(String hTreeName) throws IOException {
        HTree tree = switch (hTreeName) {
            case "url2Id" -> url2Id;
            case "id2WebNode" -> id2WebNode;
            case "word2IdTitle" -> word2IdTitle;
            case "word2IdBody" -> word2IdBody;
            case "IdBody2Word" -> IdBody2Word;
            case "IdTitle2Word" -> IdTitle2Word;
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        };
        FastIterator iter = tree.keys();
        Object key;
        while ((key = iter.next()) != null) {
            System.out.println(key + " : " + tree.get(key));
        }
        recordManager.commit();
    }
    public void printAllWithBTree(String htreeName) throws IOException{
        HTree tree = switch (htreeName) {
            case "invertedIdxTitle" -> invertedIdxTitle;
            case "invertedIdxBody" -> invertedIdxBody;
            case "forwardIdxTitle" -> forwardIdxTitle;
            case "forwardIdxBody" -> forwardIdxBody;
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        };
        FastIterator iter = tree.keys();
        Object key;
        while ((key = iter.next()) != null) {
            BTree btree = BTree.load(recordManager, (Long) tree.get(key));
            Tuple tuple = new Tuple();
            TupleBrowser browser = btree.browse();
            while (browser.getNext(tuple)) {
                System.out.println("Key: " + key);
                ((Posting)tuple.getKey()).printAll();
                System.out.println("Value: " + tuple.getValue());
            }
        }
        recordManager.commit();
    }


    public int getWordIdTitleFromStem(String stem) throws IOException {
        Object wordId = word2IdTitle.get(stem);
        recordManager.commit();
        return wordId != null ? (int) wordId : -1;
    }

    public int getWordIdBodyFromStem(String stem) throws IOException {
        Object wordId = word2IdBody.get(stem);
        recordManager.commit();
        return wordId != null ? (int) wordId : -1;
    }

    public int getWordIdTitle() {
        return wordIdTitle;
    }

    public int getWordIdBody() {
        return wordIdBody;
    }

    public int getPageIdFromURL(String url) throws IOException {
        Object pageId = url2Id.get(url);
        recordManager.commit();
        return pageId != null ? (int) pageId : -1;
    }

    public int getPageId() {
        return pageId;
    }

    // TODO: May remove after testing
    public HTree getUrl2Id() {
        return url2Id;
    }

    // TODO: May remove after testing
    public HTree getId2WebNode() {
        return id2WebNode;
    }

    // TODO: May remove after testing
    public HTree getInvertedIdxTitle() {
        return invertedIdxTitle;
    }

    /***
     * Get the value of the key from the hTree
     * @param hTreeName the name of the hTree
     * @param key the key of the value
     * @param type the type of the value
     * @return value of the key
     * @param <K> the type of the key
     * @param <V> the type of the valuew
     * @throws IOException
     *
     * For example, the following code snippet:
     * public List<WebNode> getChildren(int id) {
     *         try {
     *             String treeName = TreeName.id2WebNode.toString();
     *             return db.getEntry(treeName, id, WebNode.class).getChildren();
     *         } catch (IOException e) {
     *             e.printStackTrace();
     *             System.out.println("Error in getChildren of Crawler");
     *         }
     *         return null;
     *     }
     */
    public <K, V> V getEntry(String hTreeName, K key, Class<V> type) throws IOException { // problematic
        HTree tree = switch (hTreeName) {
            case "url2Id" -> url2Id;
            case "id2WebNode" -> id2WebNode;
            case "word2IdTitle" -> word2IdTitle;
            case "word2IdBody" -> word2IdBody;
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        };
        Object value = tree.get(key);
        if (value != null) {
            if (type.isInstance(value)) {
                recordManager.commit();
                return type.cast(value);
            } else {
                throw new ClassCastException("The object is not of type " + type.getName());
            }
        }
        recordManager.commit();
        return null;
    }

    public BTree getInvertIdxTitleEntry(int key) throws IOException {
        if (invertedIdxTitle.get(key) == null) return null;
        recordManager.commit();
        return BTree.load(recordManager, (Long) invertedIdxTitle.get(key));
    }

    public BTree getInvertIdxBodyEntry(int key) throws IOException {
        if (invertedIdxBody.get(key) == null) return null;
        recordManager.commit();
        return (BTree) invertedIdxBody.get(key);
    }

    public BTree getForwardIdxTitleEntry(int key) throws IOException {
        if (forwardIdxTitle.get(key) == null) return null;
        recordManager.commit();
        return BTree.load(recordManager, (Long) forwardIdxTitle.get(key));
    }

    public BTree getForwardIdxBodyEntry(int key) throws IOException {
        if (forwardIdxBody.get(key) == null) return null;
        recordManager.commit();
        return BTree.load(recordManager, (Long) forwardIdxBody.get(key));
    }

    public int getIdFromUrl(String url) throws IOException {
        Object id = url2Id.get(url);
        recordManager.commit();
        return id != null ? (int) id : -1;
    }
}
