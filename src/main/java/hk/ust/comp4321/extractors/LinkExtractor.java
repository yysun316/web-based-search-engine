package hk.ust.comp4321.extractors;// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser

import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;
import org.htmlparser.beans.LinkBean;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * src.src.linkExtractor.LinkExtractor extracts all the links from the given webpage
 * and prints them on standard output.
 */
public class LinkExtractor {
    /***
     * Extract all the links from a webpage
     * @param indexTable url2id, id2webnode
     * @param rootURL the URL of the webpage
     * @param numPages the number of pages to be extracted
     * @return a list of URLs of the extracted webpages that are not in the database
     * @throws Exception if the links cannot be extracted
     */
    public static List<String> extractLinks(IndexTable indexTable, String rootURL, int numPages) throws Exception {
        List<List<String>> pairList = new ArrayList<>();
        List<List<String>> pairListForRanking = new ArrayList<>();
        List<String> res = new ArrayList<>(numPages); // store the result
        LinkBean lb = new LinkBean();
        URL[] URL_array;
        Queue<String> queue = new LinkedList<>();
        queue.add(rootURL);

        while (!queue.isEmpty() && res.size() < numPages) {
            String parentURL = queue.poll();
            // get/initiate parent webnode
            WebNode parentWebNode;
            int parId = indexTable.getIdFromUrl(parentURL);
            if (parId != -1) {
                // the node already exist
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
                //compare the date to see if used
                String orignialDate = parentWebNode.getLastModifiedDate();
                String newDate = LastModifiedDateExtractor.extractModifiedDate(parentURL);
                if (LastModifiedDateExtractor.getDateInMilliseconds(orignialDate) < LastModifiedDateExtractor.getDateInMilliseconds(newDate)) {
                    parentWebNode.setLastModifiedDate(newDate);
                } else {
                    continue;
                }
                res.add(parentURL);
            } else {
                res.add(parentURL);
                parentWebNode = new WebNode(indexTable.getPageId(), parentURL, LastModifiedDateExtractor.extractModifiedDate(parentURL));
                indexTable.addEntry(TreeNames.url2Id.toString(), parentURL, indexTable.getPageId());
                indexTable.addEntry(TreeNames.id2WebNode.toString(), parentWebNode.getId(), parentWebNode);
                parId = indexTable.getEntry(TreeNames.url2Id.toString(), parentURL, Integer.class);
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
            }

            // iterate through the list of child url of parent
            // get / initiate the corresponding child webnode and set up the relationship between them
            lb.setURL(parentURL);
            URL_array = lb.getLinks();
            for (URL u : URL_array) {
                String childURL = u.toExternalForm();
                // if the parent child relationship has been set up previously then we need to do nothing
                List<String> pairForRanking = new ArrayList<>();
                pairForRanking.add(parentURL);
                pairForRanking.add(childURL);
                pairListForRanking.add(pairForRanking);
                if (!res.contains(childURL)) {
                    // check if child is already there before adding
                    List<String> chilist = parentWebNode.getChildren();
                    boolean existed = false;
                    for (String element : chilist) {
                        if (element.equals(childURL)){
                            existed = true;
                            break;
                        }
                    }
                    if(!existed){
                        parentWebNode.addChild(childURL);
                    }
                    List<String> pair = new ArrayList<>();
                    pair.add(parentURL);
                    pair.add(childURL);
                    pairList.add(pair);
                }
                if (!res.contains(childURL)) {
                    queue.add(childURL);
                }
            }
        }
        addParentLinks(indexTable, pairList);
        addParentLinksForRanking(indexTable, pairListForRanking);
        return res;
    }

    /***
     * Add parent links to the child webnode
     * Parent links and Child links are the ones to be displayed in the webpage
     * and the one for ranking is not to be displayed
     * the main difference is the one for ranking can contain child pointing back to parent
     * which is necessary when calculating the probability a user press the link and go to a page
     * @param indexTable
     * @param pairList
     * @throws IOException
     */
    public static void addParentLinks(IndexTable indexTable, List<List<String>> pairList) throws IOException {
        for (List<String> pair : pairList) {
            // check if child is already there before adding
            int childId = indexTable.getIdFromUrl(pair.get(1));
            if (childId != -1) {
                WebNode childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                List<String> parlist = childWebNode.getParent();
                boolean existed = false;
                for (String element : parlist) {
                    if (element.equals(pair.get(0))){
                        existed = true;
                        break;
                    }
                }
                if(!existed){
                    childWebNode.addParent(pair.get(0));
                    indexTable.updateEntry(TreeNames.id2WebNode.toString(), childId, childWebNode);
                }
            }
        }
    }

    /***
     * Add parent links to the child webnode for ranking
     * @param indexTable url2id, id2webnode
     * @param pairListForRanking the list of parent child pair
     * @throws IOException if the parent child relationship cannot be set up
     */
    public static void addParentLinksForRanking(IndexTable indexTable, List<List<String>> pairListForRanking)
            throws IOException {
        for (List<String> pairForRanking : pairListForRanking) {
            int childId = indexTable.getIdFromUrl(pairForRanking.get(1));
            if (childId != -1) {
                WebNode childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                // check if parent is already there before adding
                boolean existed = false;
                List<String> chilist = childWebNode.getParentForRanking();
                for (String element : chilist) {
                    if (element.equals(pairForRanking.get(0))){
                        existed = true;
                        break;
                    }
                }
                if(!existed){
                    childWebNode.addParentForRanking(pairForRanking.get(0));
                    indexTable.updateEntry(TreeNames.id2WebNode.toString(), childId, childWebNode);
                }
            }
        }
        for (List<String> pairForRanking : pairListForRanking) {
            int parId = indexTable.getIdFromUrl(pairForRanking.get(0));
            if (parId != -1) {
                WebNode parentWebnode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
                // check if child is already there before adding
                boolean existed = false;
                List<String> chilist = parentWebnode.getChildForRanking();
                for (String element : chilist) {
                    if (element.equals(pairForRanking.get(1))){
                        existed = true;
                        break;
                    }
                }
                if(!existed){
                    parentWebnode.addChildForRanking(pairForRanking.get(1));
                    indexTable.updateEntry(TreeNames.id2WebNode.toString(), parId, parentWebnode);
                }
            }
        }
    }
}