package hk.ust.comp4321.utils;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class WebNodeTest {
    private static Crawler crawler;
    private static IndexTable indexTable;
    private static ForwardInvertedIndex forwardInvertedIndex;

    @BeforeEach
    public void setUp() throws IOException {
        indexTable = new IndexTable("IndexerTest");
        crawler = new Crawler(indexTable);
        forwardInvertedIndex = new ForwardInvertedIndex("ForwardInvertedIndexTest");
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        int numPages = 5;
        crawler.extractLinks(root, numPages);
    }

    @Test
    void testNodePageRank() throws IOException, ExecutionException, InterruptedException {
        WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), 0, WebNode.class);
        currentWebNode.updatePagerank(0.5);
        Double result = currentWebNode.getPagerank();
        Assertions.assertEquals(0.5, result, "The result is correct");
    }

    @Test
    void testgetchildren() throws IOException, ExecutionException, InterruptedException {
        WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), 0, WebNode.class);
        int result = currentWebNode.getChildren().size();
        Assertions.assertEquals(4, result, "The result is correct");
    }

    @Test
    void testgetparent() throws IOException, ExecutionException, InterruptedException {
        WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), 1, WebNode.class);
        int result = currentWebNode.getParent().size();
        Assertions.assertEquals(1, result, "The result is correct");
    }

    @Test
    void testgetparentforranking() throws IOException, ExecutionException, InterruptedException {
        WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), 1, WebNode.class);
        int result = currentWebNode.getParentForRanking().size();
        Assertions.assertEquals(1, result, "The result is correct");
    }

    @Test
    void testgetchildrenforranking() throws IOException, ExecutionException, InterruptedException {
        WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), 1, WebNode.class);
        int result = currentWebNode.getChildForRanking().size();
        Assertions.assertEquals(3, result, "The result is correct");
    }
}