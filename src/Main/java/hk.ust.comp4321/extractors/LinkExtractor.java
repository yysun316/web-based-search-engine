package hk.ust.comp4321.extractors;// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser

import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;
import org.htmlparser.beans.LinkBean;

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
    public static List<String> extractLinks(IndexTable indexTable, String rootURL, int numPages) throws Exception {
        List<String> res = new ArrayList<>(numPages); // store the result
        LinkBean lb = new LinkBean();
        URL[] URL_array;
        Queue<String> queue = new LinkedList<>();
        queue.add(rootURL);

        while (!queue.isEmpty() && res.size() < numPages) {
            String parentURL = queue.poll();
            res.add(parentURL);
            // get/initiate parent webnode
            WebNode parentWebNode;
            int parId = indexTable.getIdFromUrl(parentURL);
            // status 0: initiate here
            // status 1: old page is update
            // status 2: old page is updated
            Integer pageStatus = 0;
            if (parId != -1) {
                // the node already exist
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
                //compare the date to see if used
                String orignialDate = parentWebNode.getLastModifiedDate();
                String newDate = LastModifiedDateExtractor.extractModifiedDate(parentURL);
                if (LastModifiedDateExtractor.getDateInMilliseconds(orignialDate) < LastModifiedDateExtractor.getDateInMilliseconds(newDate)) {
                    parentWebNode.setLastModifiedDate(newDate);
                    pageStatus = 2;
                } else {
                    pageStatus = 1;
                }
            } else {
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
            //System.out.println("parent URL " + parentURL);
            for (URL u : URL_array) {
                String childURL = u.toExternalForm();
                //System.out.println("has got children " + childURL);
                // if the parent child relationship has been set up previously then we need to do nothing
                if (parentWebNode.getChildren().contains(childURL)) {
                    //System.out.println("no need previously added");
                    if (!res.contains(childURL)) {
                        queue.add(childURL);
                    }
                    continue;
                }
                WebNode childWebNode;
                int childId = indexTable.getIdFromUrl(childURL);
                if (childId != -1) {
                    childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                    if((!res.contains(childURL)) && res.contains(parentURL)) {
                        childWebNode.addParent(parentURL);
                    }
                } else {
                    if(indexTable.getPageId() < numPages)
                    {
                        childWebNode = new WebNode(indexTable.getPageId(), childURL, LastModifiedDateExtractor.extractModifiedDate(childURL));
                        indexTable.addEntry(TreeNames.url2Id.toString(), childURL, indexTable.getPageId());
                        indexTable.addEntry(TreeNames.id2WebNode.toString(), childWebNode.getId(), childWebNode);
                        if((!res.contains(childURL)) && res.contains(parentURL))
                        {
                            childWebNode.addParent(parentURL);
                        }
                    }

                }
                if(!res.contains(childURL))
                {
                    parentWebNode.addChild(childURL);
                }
                if (!res.contains(childURL)) {
                    queue.add(childURL);
                }
            }
            //System.out.println(parentWebNode.getChildren());
            //System.out.println();
        }
        return res;
    }

    private static List<String> getStrings(WebNode parentWebNode, URL[] URL_array) {
        List<String> parentsExtraChildList = new ArrayList<>();
        for (String element : parentWebNode.getChildren()) {
            parentsExtraChildList.add(new String(element));
        }
        for (URL u : URL_array) {
            String childURL = u.toExternalForm();
            parentsExtraChildList.remove(childURL);
        }
        if (parentsExtraChildList.size() != 0) {
            //System.out.println("something needs to be removed");
        }
        return parentsExtraChildList;
    }
}