//package hk.ust.comp4321;
//
//import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
//import hk.ust.comp4321.invertedIndex.IndexTable;
//import hk.ust.comp4321.utils.TreeNames;
//import hk.ust.comp4321.utils.WebNode;
//
//public class SearchEngine {
//    private static IndexTable db1;
//    //private static ForwardInvertedIndex db2;
//
//    public static void main(String[] args) throws Exception {
//        String rootURL = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
//        db1 = new IndexTable("CrawlerDatabase");
//        //db2 = new ForwardInvertedIndex("ForwardInvertedIndexDatabase");
//        for (int key = 0; key < 5; key++)
//        {
//            System.out.println("key is " + key);
//            for (String childURL : db1.getEntry(TreeNames.id2WebNode.toString(), key, WebNode.class).getChildren())
//            {
//                System.out.println(childURL);
//            }
//        }
//
//        PageRankByLink(db1, rootURL);
//        for (int key = 0; key < 5; key++)
//        {
//            System.out.println("key is " + key);
//            for (String childURL : db1.getEntry(TreeNames.id2WebNode.toString(), key, WebNode.class).getChildren())
//            {
//                System.out.println(childURL);
//            }
//        }
//
//
//
////        String query = "movie movie";
////        List<Double> store = PageRankByStem(db1, db2, query);
////        System.out.println(store);
////        List<Integer> wantedPagesId = PageRankByBoth(db1,store);
////        System.out.println(wantedPagesId);
////        System.out.println(getLinkWeights(db1));
//    }
//}