package hk.ust.comp4321.invertedIndex;

import hk.ust.comp4321.utils.WebNode;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.List;

public class CrawlerInvertedIndex implements InvertedIndex {
    private RecordManager recordManager;
    private HTree url2Id;
    private HTree id2WebNode;

    public CrawlerInvertedIndex(String recordManger, String url2Id, String id2webNode) throws IOException {
        recordManager = RecordManagerFactory.createRecordManager(recordManger);
        long recId1 = recordManager.getNamedObject(url2Id); // get the id of the object named objName
        long recId2 = recordManager.getNamedObject(id2webNode); // get the id of the object named objName
        if (recId1 != 0) this.url2Id = HTree.load(recordManager, recId1);
        else {
            this.url2Id = HTree.createInstance(recordManager);
            recordManager.setNamedObject(url2Id, this.url2Id.getRecid());
        }
        if (recId2 != 0) this.id2WebNode = HTree.load(recordManager, recId2);
        else {
            this.id2WebNode = HTree.createInstance(recordManager);
            recordManager.setNamedObject(id2webNode, this.id2WebNode.getRecid());
        }
    }

    public void close() throws IOException { // change finalize to close
        recordManager.commit();
        recordManager.close();
    }

    /***
     * Add or update an entry to the hashtable
     * @param obj1 the key
     * @param obj2 the value
     * @throws IOException
     */
    @Override
    public void addEntry(Object obj1, Object obj2) throws IOException {
        if (obj1 instanceof Integer && obj2 instanceof WebNode node) {
            int id = (Integer) obj1;
            id2WebNode.put(id, node);
        } else if (obj1 instanceof String url && obj2 instanceof Integer) {
            int id = (Integer) obj2;
            url2Id.put(url, id);
        }
    }

    /***
     * Check if the url exits in the hashtable
     * @param url
     * @return true if the url exists, false otherwise
     * @throws IOException
     */
    public boolean containsURL(String url) throws IOException {
        return url2Id.get(url) != null;
    }

    @Override
    public void delEntry(Object obj) throws IOException {
        if (obj instanceof String url) {
            url2Id.remove(url);
        } else if (obj instanceof Integer id) {
            id2WebNode.remove(id);
        }
    }

    @Override
    public void printAll() throws IOException {
        FastIterator iter = url2Id.keys();
        String key;
        while ((key = (String) iter.next()) != null) {
            System.out.println(key + " : " + url2Id.get(key));
        }
    }

    public List<WebNode> getChildren(int id) throws IOException {
        if (id2WebNode.get(id) == null) return null;
        return ((WebNode) id2WebNode.get(id)).getChildren();
    }


    public String getLastModifiedDate(String url) {
        try {
            if (!containsURL(url)) return null;
            WebNode node = (WebNode) id2WebNode.get(url2Id.get(url));
            return node.getLastModifiedDate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebNode getNodeFromURL(String url) {
        try {
            if (!containsURL(url)) return null;
            return (WebNode) id2WebNode.get(url2Id.get(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //    public static void main(String[] args) {
//        try {
//            InvertedIndex index = new InvertedIndex("lab1", "ht1");
//
//            index.addEntry("cat", 2, 6);
//            index.addEntry("dog", 1, 33);
//            System.out.println("First print");
//            index.printAll();
//
//            index.addEntry("cat", 8, 3);
//            index.addEntry("dog", 6, 73);
//            index.addEntry("dog", 8, 83);
//            index.addEntry("dog", 10, 5);
//            index.addEntry("cat", 11, 106);
//            System.out.println("Second print");
//            index.printAll();
//
//            index.delEntry("dog");
//            System.out.println("Third print");
//            index.printAll();
//            index.finalize();
//        } catch (IOException ex) {
//            System.err.println(ex.toString());
//        }
//}
}