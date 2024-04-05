package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import hk.ust.comp4321.extractors.PageSizeExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.IndexerPhases;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static hk.ust.comp4321.utils.PageRank1.*;


public class ProjectPhase2 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static ForwardInvertedIndex db2;
    private static ForwardInvertedIndex db3;
    private static Indexer indexer;
    private static IndexerPhases indexerPhases;
    private static StopStem stopStem;


    public static void main(String[] args) throws Exception {

        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        stopStem = new StopStem("resources/stopwords.txt");
        try {
            db1 = new IndexTable("EmCrawlerDatabase");
            db2 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabase");
            db3 = new ForwardInvertedIndex("EmForwardInvertedIndexDatabasePhases");
            indexer = new Indexer(db1, db2);
            indexerPhases = new IndexerPhases(db1, db2, db3);
            crawler1 = new Crawler(db1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int numPages = 15;
        List<String> result = crawler1.extractLinks(rootURL, numPages);
        System.out.println(result);
        for (String currentUrl : result) {
            System.out.println(currentUrl);
            indexer.index(currentUrl);
            indexerPhases.indexPhases(currentUrl);
        }
        System.out.println("cse depart is at " + db3.getWordIdTitleFromStem("cse depart"));
        System.out.println("cse depart is stemmed as " + db3.getWordFromIdTitle(1));
        System.out.println("link weights are: " + getLinkWeights(db1));
        PageRankByLink(db1,0.5);
        System.out.println("link weights are: " + getLinkWeights(db1));
        PageRankByLink(db1,0.5);
        System.out.println("link weights are: " + getLinkWeights(db1));
        List<List<Double>> weighttp = RankStem(db1,db3,1);
        List<List<Double>> weightbp = RankStem(db1,db3,2);
        List<List<Double>> weightt = RankStem(db1,db2,1);
        List<List<Double>> weightb = RankStem(db1,db2,2);
        List<Double> scoret = RankStemWithQuery(db1,db2,"CSE department of HKUST",1, weightt,0,0);
        List<Double> scoreb = RankStemWithQuery(db1,db2,"CSE department of HKUST",2, weightb,0,0);
        //System.out.println("hkust got id " + db2.getWordIdBodyFromStem("hkust"));
        //System.out.println("448 is " + db2.getWordFromIdBody(448));
        //System.out.println("1447 is " + db2.getWordFromIdBody(1447));
        List<Double> scoretp = RankStemWithQuery(db1,db3,"CSE department of HKUST",1, weighttp,1,2);
        List<Double> scorebp = RankStemWithQuery(db1,db3,"CSE department of HKUST",2, weightbp,1,2);
        //CheckPageRank(db1, db2, "computer dog", 1,scoret);
        //CheckPageRank(db1, db2, "computer dog", 2,scoreb);
        List<Integer> resultRanking = PageRankByBoth(db1, scoret, scoreb, scoretp, scorebp, 0.0, 0.0, 5.0, 3.0);
        //List<Integer> resultRanking = PageRankByBoth(db1, scoret, scoreb, 20.0, 10.0);
        //System.out.println("resulted ranking is: "+ resultRanking);
        //for (Integer rankpage : resultRanking)
        for (int rankpage = 0; rankpage < 10; rankpage++)
        {
            WebNode currentWebNode = db1.getEntry(TreeNames.id2WebNode.toString(), rankpage, WebNode.class);
            System.out.println(TitleExtractor.extractTitle(currentWebNode.getUrl()));
            System.out.println(currentWebNode.getUrl());
//            System.out.println(currentWebNode.getLastModifiedDate());
//            System.out.println(PageSizeExtractor.extractPageSize(currentWebNode.getUrl()));
            Map<String, Integer> keyword2Freq;
            keyword2Freq = db2.getKeywordFrequency(rankpage, 50, 0);
            keyword2Freq.forEach((k, v) -> System.out.print(k + " " + v + "; "));
            System.out.println();
            System.out.println("parent" + currentWebNode.getParent());
            System.out.println("child" + currentWebNode.getChildren());
            System.out.println();
        }
        db1.close();
    }
}