package hk.ust.comp4321.crawler;/* --
COMP4321 Project
Students' Name: Choi Hei Ting, Yeung Yu San
Student ID: 20856508, 20861929
Section: LX
Email: htchoiad@connect.ust.hk
       ysyeungad@connect.ust.hk
*/

import hk.ust.comp4321.extractors.LinkExtractor;
import hk.ust.comp4321.invertedIndex.CrawlerInvertedIndex;
import hk.ust.comp4321.utils.WebNode;

import java.io.IOException;
import java.util.List;


public class Crawler {
    private int curMaxID;
    private CrawlerInvertedIndex crawlerDB;

    public Crawler() {
        curMaxID = 0;
        try {
            crawlerDB = new CrawlerInvertedIndex("crawler", "url2Id", "id2WebNodes");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in Crawler constructor");
        }
    }

    public List<WebNode> extractLinks(String root, int numPages) {
        try {
            List<WebNode> webNodes = LinkExtractor.extractLinks(crawlerDB, root, numPages, curMaxID);
            curMaxID += webNodes.size();
            return webNodes;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in extractLinks of Crawler");
        }
        return null;
    }

    public List<WebNode> getChildren(int id) {
        try {
            return crawlerDB.getChildren(id);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in getChildren of Crawler");
        }
        return null;
    }
//    private String url;
//    int numPage;

//    public Crawler(String url, int numPage) {
//        this.url = url;
//        this.numPage = numPage;
//    }


//    public Vector<String> extractWords() throws ParserException {
//        // extract words in url and return them
//        // use StringTokenizer to tokenize the result from StringBean
//        // ADD YOUR CODES HERE
//        //Vector<String> tempWordVector = new Vector();
//        Vector<String> tempWordVector = new Vector<>();
//
//        //StringExtractor se = new StringExtractor(url);
//        //tempWordVector.add(se.extractStrings(false));
//
//        StringBean sb = new StringBean();
//        sb.setURL(url);
//        String passage = sb.getStrings();
//
//        String[] String_array = passage.split(" ");
//        for (int i = 0; i < String_array.length; i++) {
//            tempWordVector.add(String_array[i]);
//        }
//
//        return tempWordVector;
//    }
//
//    public Vector<String> extractLinks() throws ParserException {
//        // extract links in url and return them
//        // ADD YOUR CODES HERE
//        //Vector<String> tempLinkVector = new Vector();
//        Vector<String> tempLinkVector = new Vector<String>();
//
//        LinkBean lb = new LinkBean();
//        lb.setURL(url);
//        URL[] URL_array = lb.getLinks();
//        for (int i = 0; i < URL_array.length; i++) {
//            tempLinkVector.add(URL_array[i].toString());
//        }
//
//        return tempLinkVector;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public int getNumPage() {
//        return numPage;
//    }

    //    public static void main(String[] args) {
//        try {
//            Crawler crawler = new Crawler("http://www.cs.ust.hk/~dlee/4321/");
//
//
//            Vector<String> words = crawler.extractWords();
//
//            System.out.println("Words in " + crawler.url + " (size = " + words.size() + ") :");
//            for (int i = 0; i < words.size(); i++)
//                if (i < 5 || i > words.size() - 6) {
//                    System.out.println(words.get(i));
//                } else if (i == 5) {
//                    System.out.println("...");
//                }
//            System.out.println("\n\n");
//
//
//            Vector<String> links = crawler.extractLinks();
//            System.out.println("Links in " + crawler.url + ":");
//            for (int i = 0; i < links.size(); i++)
//                System.out.println(links.get(i));
//            System.out.println("");
//
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//
//    }
}

	
