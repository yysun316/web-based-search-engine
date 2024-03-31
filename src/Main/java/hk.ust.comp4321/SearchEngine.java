package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static hk.ust.comp4321.utils.PageRank.*;

public class SearchEngine {
    private static IndexTable db1;
    private static ForwardInvertedIndex db2;

    public static void main(String[] args) throws Exception {
        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        db1 = new IndexTable("crawlerEmTest");
        db2 = new ForwardInvertedIndex("whatever");
        PageRankByLink(db1, rootURL);
//        String query = "movie movie";
//        List<Double> store = PageRankByStem(db1, db2, query);
//        System.out.println(store);
//        List<Integer> wantedPagesId = PageRankByBoth(db1,store);
//        System.out.println(wantedPagesId);
//        System.out.println(getLinkWeights(db1));
    }
}