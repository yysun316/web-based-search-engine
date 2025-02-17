package hk.ust.comp4321.crawler;/* --
COMP4321 Project
Students' Name: Choi Hei Ting, Yeung Yu San
Student ID: 20856508, 20861929
Section: LX
Email: htchoiad@connect.ust.hk
       ysyeungad@connect.ust.hk
*/

import hk.ust.comp4321.extractors.LinkExtractor;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Crawler {
    private IndexTable db;

    public Crawler(IndexTable db) {
        this.db = db;
    }

    public List<String> extractLinks(String root, int numPages) {
        try {
            return LinkExtractor.extractLinks(db, root, numPages);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in extractLinks of Crawler");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // TODO: Given a parent pageId, return the list of children pageIds
    public List<Integer> getChildrenPageIds(int id) {
        try {
            System.out.println("try getchildrenpageid");
            List<Integer> pageIds = new ArrayList<>();
            String treeName = TreeNames.id2WebNode.toString();
            for (String url : db.getEntry(treeName, id, WebNode.class).getChildren()) {
                System.out.println("ChildrenPageURL is " + url);
                pageIds.add(db.getEntry(TreeNames.url2Id.toString(), url, Integer.class));
                System.out.println("ChildrenPageID is " + db.getEntry(TreeNames.url2Id.toString(), url, Integer.class));
            }
            return pageIds;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in getChildren of Crawler");
        }
        return null;
    }

    // TODO: Given a child's pageId, return the parent pageId
    public List<Integer> getParentPageId(int id) {
        try {
            List<Integer> pageIds = new ArrayList<>();
            String treeName = TreeNames.id2WebNode.toString();
            for (String url : db.getEntry(treeName, id, WebNode.class).getParent())
                pageIds.add(db.getEntry(TreeNames.url2Id.toString(), url, Integer.class));
            return pageIds;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in getParent of Crawler");
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

	
