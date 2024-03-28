package hk.ust.comp4321.indexer;

import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.WebNode;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class IndexerTest {
    private Indexer indexer;
    private IndexTable indexTable;

    @Test
    @Order(1)
    public void testIndexWebsite() {
        try {
            // Assume crawler has already crawled the website and add a node to the database
            indexTable = new IndexTable("recordManagerTest");
            indexer = new Indexer(indexTable);
//            String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
            String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm";
//            String url = "https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_title_test";
//            String url = "https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_head_test";
            int id = indexTable.getPageId();
            WebNode node = new WebNode(id, null, url, LastModifiedDateExtractor.extractModifiedDate(url));
            indexTable.addEntry("url2Id", url, id);
            indexTable.addEntry("id2WebNode", id, node);

            // index the title of the website
            indexer.index(url);
            // Check if the title has been indexed correctly
            // Check forward index
            System.out.println("Checking forward index:");
            indexTable.printAllWithBTree("forwardIdxTitle");
            System.out.println("End of forward index\n");
//             Check inverted index
//            Posting: pageId, freq
//            wordId -> (posting, pos)
//            0 -> ((0, 1) -> (0))
//            1 -> ((0, 1) -> (1))
            System.out.println("Checking inverted index:");
            indexTable.printAllWithBTree("invertedIdxTitle");
            System.out.println("End of inverted index\n");


            // check forward index body
            System.out.println("Checking forward index body:");
            indexTable.printAllWithBTree("forwardIdxBody");
            System.out.println("End of forward index body\n");
            // check inverted index body
            System.out.println("Checking inverted index body:");
            indexTable.printAllWithBTree("invertedIdxBody");
            System.out.println("End of inverted index body\n");

        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}