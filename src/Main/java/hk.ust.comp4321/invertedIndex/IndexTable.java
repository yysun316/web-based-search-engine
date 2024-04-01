package hk.ust.comp4321.invertedIndex;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;


public class IndexTable {
    private RecordManager recordManager;
    // for Crawler:
    private HTree url2Id; // url to id {using the url to find the id}
    private HTree id2WebNode; // id to webNode object {using the id to find the webNode}
    private int pageId; // increase by 1 for each new page (url2ID add entry)
    // for Indexer:
//    private HTree word2IdTitle; // word to id {using the word to find the wordId}
//    private HTree word2IdBody; // word to id {using the word to find the wordId}
//    private HTree invertedIdxTitle; // wordID to list of {id} (tell which webpages contain the word)
//    private HTree invertedIdxBody; // wordID to list of {id} for that word in body
//    private HTree forwardIdxTitle; // id to wordID list for that page {using the id to find the wordId, sort the wordId by frequency}
//    private HTree forwardIdxBody; // id to wordID list for that page {using the id to find the wordId}
//    private int wordIdTitle; // increase by 1 for each new word
//    private int wordIdBody; // increase by 1 for each new word
//    private HTree IdTitle2Word; // wordId to word {using the wordId to find the word}
//    private HTree IdBody2Word; // wordId to word {using the wordId to find the word}

    public IndexTable(String recordManagerName) throws IOException {
        recordManager = RecordManagerFactory.createRecordManager(recordManagerName);

        long recId1 = recordManager.getNamedObject("url2Id");
        long recId2 = recordManager.getNamedObject("id2WebNode");
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
        pageId = getSize(url2Id);
//        long recId3 = recordManager.getNamedObject("word2IdTitle");
//        long recId4 = recordManager.getNamedObject("word2IdBody");
//        long recId5 = recordManager.getNamedObject("invertedIdxTitle");
//        long recId6 = recordManager.getNamedObject("invertedIdxBody");
//        long recId7 = recordManager.getNamedObject("forwardIdxTitle");
//        long recId8 = recordManager.getNamedObject("forwardIdxBody");
//        long recId9 = recordManager.getNamedObject("IdTitle2Word");
//        long recId10 = recordManager.getNamedObject("IdBody2Word");


//        if (recId3 != 0) this.word2IdTitle = HTree.load(recordManager, recId3);
//        else {
//            this.word2IdTitle = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("word2IdTitle", this.word2IdTitle.getRecid());
//        }
//
//        if (recId4 != 0) this.word2IdBody = HTree.load(recordManager, recId4);
//        else {
//            this.word2IdBody = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("word2IdBody", this.word2IdBody.getRecid());
//        }
//
//        if (recId5 != 0) this.invertedIdxTitle = HTree.load(recordManager, recId5);
//        else {
//            this.invertedIdxTitle = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("invertedIdxTitle", this.invertedIdxTitle.getRecid());
//        }
//
//        if (recId6 != 0) this.invertedIdxBody = HTree.load(recordManager, recId6);
//        else {
//            this.invertedIdxBody = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("invertedIdxBody", this.invertedIdxBody.getRecid());
//        }
//
//        if (recId7 != 0) this.forwardIdxTitle = HTree.load(recordManager, recId7);
//        else {
//            this.forwardIdxTitle = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("forwardIdxTitle", this.forwardIdxTitle.getRecid());
//        }
//
//        if (recId8 != 0) this.forwardIdxBody = HTree.load(recordManager, recId8);
//        else {
//            this.forwardIdxBody = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("forwardIdxBody", this.forwardIdxBody.getRecid());
//        }
//        if (recId9 != 0) this.IdTitle2Word = HTree.load(recordManager, recId9);
//        else {
//            this.IdTitle2Word = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("IdTitle2Word", this.IdTitle2Word.getRecid());
//        }
//        if (recId10 != 0) this.IdBody2Word = HTree.load(recordManager, recId10);
//        else {
//            this.IdBody2Word = HTree.createInstance(recordManager);
//            recordManager.setNamedObject("IdBody2Word", this.IdBody2Word.getRecid());
//        }
//        wordIdTitle = getSize(word2IdTitle);
//        wordIdBody = getSize(word2IdBody);
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

//    /***
//     * Close the record manager
//     * @throws IOException if the record manager is not closed
//     */
//    public void close() throws IOException { // change finalize to close
//        recordManager.commit();
//        recordManager.close();
//    }

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
//            case "word2IdTitle" -> {
//                if (word2IdTitle.get(key) == null)
//                {
//                    word2IdTitle.put(key, wordIdTitle++);
//                }
//                else System.out.println("The word already exists in the title database.");
//            }
//            case "word2IdBody" -> {
//                if (word2IdBody.get(key) == null)
//                    word2IdBody.put(key, wordIdBody++);
//                else System.out.println("The word already exists in the body database.");
//            }
//            case "IdTitle2Word" -> {
//                if (IdTitle2Word.get(key) == null) IdTitle2Word.put(key, value);
//                else System.out.println("The word already exists in the IdTitle2Word database.");
//            }
//            case "IdBody2Word" -> {
//                if (IdBody2Word.get(key) == null) IdBody2Word.put(key, value);
//                else System.out.println("The word already exists in the IdBody2Word database.");
//            }
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    public <K, V> void updateEntry(String hTreeName, K key, V value) throws IOException {
        switch (hTreeName) {
            case "id2WebNode" -> id2WebNode.put(key, value);
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }

    public int getPageIdFromURL(String url) throws IOException {
        Object pageId = url2Id.get(url);
        //recordManager.commit();
        return pageId != null ? (int) pageId : -1;
    }

    public static int getPageId() {
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
//            case "word2IdTitle" -> word2IdTitle;
//            case "word2IdBody" -> word2IdBody;
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        };
        Object value = tree.get(key);
        if (value != null) {
            if (type.isInstance(value)) {
                //recordManager.commit();
                return type.cast(value);
            } else {
                throw new ClassCastException("The object is not of type " + type.getName());
            }
        }
        //recordManager.commit();
        return null;
    }

//    public BTree getInvertIdxTitleEntry(int key) throws IOException {
//        if (invertedIdxTitle.get(key) == null) return null;
//        //recordManager.commit();
//        return BTree.load(recordManager, (Long) invertedIdxTitle.get(key));
//    }
//
//    public BTree getInvertIdxBodyEntry(int key) throws IOException {
//        if (invertedIdxBody.get(key) == null) return null;
//        //recordManager.commit();
//        return (BTree) invertedIdxBody.get(key);
//    }
//
//    public BTree getForwardIdxTitleEntry(int key) throws IOException {
//        if (forwardIdxTitle.get(key) == null) return null;
//        //recordManager.commit();
//        return BTree.load(recordManager, (Long) forwardIdxTitle.get(key));
//    }
//
//    public BTree getForwardIdxBodyEntry(int key) throws IOException {
//        if (forwardIdxBody.get(key) == null) return null;
//        //recordManager.commit();
//        return BTree.load(recordManager, (Long) forwardIdxBody.get(key));
//    }

    public int getIdFromUrl(String url) throws IOException {
        Object id = url2Id.get(url);
        //recordManager.commit();
        return id != null ? (int) id : -1;
    }
    public void close() throws IOException {
        recordManager.close();
    }

}
