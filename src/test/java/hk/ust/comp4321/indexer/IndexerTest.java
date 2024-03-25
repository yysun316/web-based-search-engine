package hk.ust.comp4321.indexer;

import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.Posting;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class IndexerTest {
    private Indexer indexer;
    private IndexTable indexTable;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("recordManagerTest");
        indexer = new Indexer(indexTable);
    }

    @Test
    public void testIndexWebsite() {
        try {
            indexer.index("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
            BTree value = indexTable.getForwardIdxTitleEntry(1);
            Tuple tuple = new Tuple();
            TupleBrowser browser = value.browse();
            while (browser.getNext(tuple)) {
                System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getValue());
                System.out.println("WordId:" + ((Posting) tuple.getKey()).getId() + ", PageId: " + ((Posting) tuple.getKey()).getFreq());;
            }

            value = indexTable.getInvertIdxTitleEntry(1);
            tuple = new Tuple();
            browser = value.browse();

            while (browser.getNext(tuple)) {
                System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getValue());
                assertEquals(1, tuple.getKey());
            }
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}