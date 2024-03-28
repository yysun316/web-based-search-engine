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
            indexTable.addEntry("url2Id", "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm", indexTable.getPageId());
            indexTable.addEntry("url2Id", "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm", indexTable.getPageId());
            indexTable.addEntry("url2Id", "https://www.cse.ust.hk/~kwtleung/COMP4321/news.htm", indexTable.getPageId());
            // Retrieve the entry
            Integer pageId1 = indexTable.getEntry("url2Id", "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm", Integer.class);
            Integer pageId2 = indexTable.getEntry("url2Id", "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm", Integer.class);
            Integer pageId3 = indexTable.getEntry("url2Id", "https://www.cse.ust.hk/~kwtleung/COMP4321/news.htm", Integer.class);

            // Check if the entry has been added and retrieved correctly
            HTree url2Id = indexTable.getUrl2Id();
            FastIterator iter = url2Id.keys();
            String key;
            while ((key = (String) iter.next()) != null) {
                System.out.println(key + " " + url2Id.get(key));
            }
            assertEquals(0, pageId1);
            assertEquals(1, pageId2);
            assertEquals(2, pageId3);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testAddEntryToInvertedIdx() {
        try {
            ArrayList<Integer> pos = new ArrayList<>();
            pos.add(1);
            pos.add(2);
            pos.add(3);
            indexTable.updateInvertedIdx("invertedIdxTitle", 0, 0, 3, pos);
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
            indexTable.updateForwardIdx("forwardIdxTitle", 1, 1, 3, pos);
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