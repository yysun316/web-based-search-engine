package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;

import java.io.IOException;
import java.util.List;

import static hk.ust.comp4321.utils.PageRank1.*;


public class ProjectPhase2 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static ForwardInvertedIndex db2;
    private static Indexer indexer;
    private static StopStem stopStem;


    public static void main(String[] args) throws Exception {

        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        stopStem = new StopStem("resources/stopwords.txt");
        try {
            db1 = new IndexTable("EmCrawlerDatabase");
            db2 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabase");
            indexer = new Indexer(db1, db2);
            crawler1 = new Crawler(db1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int numPages = 30;
        List<String> result = crawler1.extractLinks(rootURL, numPages);
        for (String currentUrl : result) {
            indexer.index(currentUrl);
        }
        System.out.println("link weights are: " + getLinkWeights(db1));
        PageRankByLink(db1);
        System.out.println("link weights are: " + getLinkWeights(db1));
        List<List<Double>> weightt = RankStem(db1,db2,1);
        List<List<Double>> weightb = RankStem(db1,db2,2);
        List<Double> scoret = RankStemWithQuery(db1,db2,"computer dog",1, weightt);
        List<Double> scoreb = RankStemWithQuery(db1,db2,"computer dog",2, weightb);
        //CheckPageRank(db1, db2, "computer dog", 1,scoret);
        //CheckPageRank(db1, db2, "computer dog", 2,scoreb);
        List<Integer> resultRanking = PageRankByBoth(db1, scoret, scoreb, 20.0, 10.0);
        System.out.println("resulted ranking is: "+ resultRanking);
        db1.close();
    }

}