package hk.ust.comp4321.invertedIndex;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;

/***
 * The class to manage the database for crawler.
 * The database contains two hTrees: url2Id and id2WebNode.
 * url2Id: url to id {using the url to find the id}
 * id2WebNode: id to webNode object {using the id to find the webNode}
 */
public class IndexTable {
    private RecordManager recordManager;
    private HTree url2Id; // url to id {using the url to find the id}
    private HTree id2WebNode; // id to webNode {using the id to find the webNode}
    private int pageId; // increase by 1 for each new page (url2ID add entry)

    /***
     * Constructor of the class
     * @param recordManagerName the name of the record manager
     * @throws IOException if the record manager is not found
     */
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
            case "url2Id" -> {
                if (url2Id.get(key) == null) url2Id.put(key, pageId++);
                else System.out.println("The URL already exists in the database.");
            }
            case "id2WebNode" -> {
                if (id2WebNode.get(key) == null) id2WebNode.put(key, value); // non-serializable object
                else System.out.println("The WebNode already exists in the database.");
            }
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }
    /***
     * Update an entry in the hTree
     * @param hTreeName the name of the hTree
     * @param key the key of the value
     * @param value the value of the key
     * @param <K> the type of the key
     * @param <V> the type of the value
     * @throws IOException if the tree name is invalid
     */
    public <K, V> void updateEntry(String hTreeName, K key, V value) throws IOException {
        switch (hTreeName) {
            case "id2WebNode" -> id2WebNode.put(key, value);
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        }
        recordManager.commit();
    }
    /***
     * Get the page id from the URL
     * @param url the URL of the page
     * @return the page id
     * @throws IOException if the URL is not found
     */
    public int getPageIdFromURL(String url) throws IOException {
        Object pageId = url2Id.get(url);
        return pageId != null ? (int) pageId : -1;
    }
    /***
     * Get the current largest pageId + 1
     */
    public int getPageId() {
        return pageId;
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
            default -> throw new IllegalArgumentException("Invalid hTreeName");
        };
        Object value = tree.get(key);
        if (value != null) {
            if (type.isInstance(value)) {
                return type.cast(value);
            } else {
                throw new ClassCastException("The object is not of type " + type.getName());
            }
        }
        return null;
    }

    /***
     * Get the id from the URL
     * @param url the URL of the page
     * @return the id of the URL
     * @throws IOException if the URL is not found
     */
    public int getIdFromUrl(String url) throws IOException {
        Object id = url2Id.get(url);
        return id != null ? (int) id : -1;
    }

    /***
     * Close the record manager
     * @throws IOException if the record manager is not found
     */
    public void close() throws IOException {
        recordManager.close();
    }

    /***
     * Get the url2Id hTree
     * @return the url2Id hTree
     */
    public HTree getUrl2Id() {
        return url2Id;
    }

    /***
     * Get the id2WebNode hTree
     * @return the id2WebNode hTree
     */
    public HTree getId2WebNode() {
        return id2WebNode;
    }

}
