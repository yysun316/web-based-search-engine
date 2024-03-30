//package hk.ust.comp4321;
//
//import hk.ust.comp4321.crawler.Crawler;
//import hk.ust.comp4321.indexer.Indexer;
//import hk.ust.comp4321.invertedIndex.IndexTable;
//
//import static hk.ust.comp4321.utils.PageRank.PageRank;
//
//public class SearchEngine {
//    private static IndexTable db1;
//    private static Indexer indexer;
//    private static Crawler crawler1;
//    public static void main(String[] args) throws Exception {
//        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
//        db1 = new IndexTable("crawlerEmTest");
//        indexer = new Indexer(db1);
//        crawler1 = new Crawler(db1);
//        PageRank(db1, rootURL);
//
//    }
//}