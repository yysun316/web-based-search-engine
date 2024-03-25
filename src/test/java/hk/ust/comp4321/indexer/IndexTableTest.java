package hk.ust.comp4321.indexer;

import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.Posting;
import jdbm.btree.BTree;
import jdbm.helper.FastIterator;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.htree.HTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class IndexTableTest {
    private IndexTable indexTable;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("testRecordManager");
    }

    @Test
    public void testAddAndGetEntry() {
        try {
            // Add an entry to the url2Id HTree
            indexTable.addEntry("url2Id", "http://test.com", indexTable.getPageId());
            indexTable.addEntry("url2Id", "http://test.com", indexTable.getPageId());
            indexTable.addEntry("url2Id", "http://www.example.com", indexTable.getPageId());
            // Retrieve the entry
            Integer pageId = indexTable.getEntry("url2Id", "http://test.com", Integer.class);
            Integer pageId2 = indexTable.getEntry("url2Id", "http://www.example.com", Integer.class);
            // Check if the entry has been added and retrieved correctly

            HTree url2Id = indexTable.getUrl2Id();
            FastIterator iter = url2Id.keys();
            String key;
            while ((key = (String) iter.next()) != null) {
                System.out.println(key + " " + url2Id.get(key));
            }
            assertEquals(0, pageId);
            assertEquals(1, pageId2);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testAddEntryToInvertedIdx() {
        try {
            // Add an entry to the invertedIdxTitle HTree
            indexTable.updateInvertedIdx("invertedIdxTitle", 1, 1, 1);
            // Retrieve the entry
            BTree value = indexTable.getInvertIdxTitleEntry(1);

            // Assume that 'btree' is an instance of BTree
            Tuple tuple = new Tuple();
            TupleBrowser browser = value.browse();

            while (browser.getNext(tuple)) {
                System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getValue());
                assertEquals(1, tuple.getKey());
            }

        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testAddEntryToForwardIdx() {
        try {
            // Add an entry to the forwardIdxTitle HTree
            ArrayList<Integer> pos = new ArrayList<>();
            pos.add(1);
            pos.add(2);
            pos.add(3);
            indexTable.updateForwardIdx("forwardIdxTitle", 1, 1, pos);
            // Retrieve the entry
            BTree value = indexTable.getForwardIdxTitleEntry(1);
            Tuple tuple = new Tuple();
            TupleBrowser browser = value.browse();

            while (browser.getNext(tuple)) {
                System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getValue());
                System.out.println("WordId:" + ((Posting) tuple.getKey()).getId() + ", PageId: " + ((Posting) tuple.getKey()).getFreq());
                assertEquals(pos, tuple.getValue());
            }

        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}