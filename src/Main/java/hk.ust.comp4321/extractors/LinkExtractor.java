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
            if (parId != -1)
            {
                // the node already exist
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
                //compare the date to see if used
                String orignialDate = parentWebNode.getLastModifiedDate();
                String newDate = LastModifiedDateExtractor.extractModifiedDate(parentURL);
                if(LastModifiedDateExtractor.getDateInMilliseconds(orignialDate) < LastModifiedDateExtractor.getDateInMilliseconds(newDate))
                {
                    parentWebNode.setLastModifiedDate(newDate);
                    pageStatus = 2;
                }
                else
                {
                    pageStatus = 1;
                }
            }
            else
            {
                parentWebNode = new WebNode(indexTable.getPageId(), parentURL, LastModifiedDateExtractor.extractModifiedDate(parentURL));
                indexTable.addEntry(TreeNames.url2Id.toString(), parentURL, indexTable.getPageId());
                indexTable.addEntry(TreeNames.id2WebNode.toString(), parentWebNode.getId(), parentWebNode);
                parId = indexTable.getEntry(TreeNames.url2Id.toString(), parentURL, Integer.class);
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
            }
//            // Compare modified date for visited pages
//            if (indexTable.getIdFromUrl(parentURL) != -1)
//                compareVisitedPages(indexTable, url, res, parentID);
//                // Handle unvisited pages
//            else {
//                res.add(url);
//                WebNode node = new WebNode(indexTable.getPageId(), parentID, url, LastModifiedDateExtractor.extractModifiedDate(url));
//                indexTable.addEntry(TreeNames.url2Id.toString(), url, indexTable.getPageId());
//                indexTable.addEntry(TreeNames.id2WebNode.toString(), node.getId(), node);
//                indexTable.getEntry(TreeNames.id2WebNode.toString(), parentID, WebNode.class).addChild(node.getId());
//            }
            // BFS the webpages

            // create parentsExtraChildList which stores parent child relationship that is no longer there and should be removed
            if(pageStatus==2)
            {
                System.out.println("the page is old");
                lb.setURL(parentURL);
                URL_array = lb.getLinks();
                List<String> parentsExtraChildList = getStrings(parentWebNode, URL_array);
                // iterate through parentsExtraChildList, get the child, and remove the relationship
                for(String extraElement : parentsExtraChildList){
                    parentWebNode.removeChild(extraElement);
                    // get child webnode and remove its corresponding parent
                    WebNode extraWebNode;
                    int extraId = indexTable.getIdFromUrl(extraElement);
                    if (extraId != -1)
                    {
                        extraWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), extraId, WebNode.class);
                        extraWebNode.removeParent(parentURL);
                    }
                }
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
                if (childId != -1)
                {
                    childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                }
                else
                {
                    childWebNode = new WebNode(indexTable.getPageId(), childURL, LastModifiedDateExtractor.extractModifiedDate(childURL));
                    indexTable.addEntry(TreeNames.url2Id.toString(), childURL, indexTable.getPageId());
                    indexTable.addEntry(TreeNames.id2WebNode.toString(), childWebNode.getId(), childWebNode);
                }
                parentWebNode.addChild(childURL);
                childWebNode.addParent(parentURL);
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
        if (parentsExtraChildList.size()!=0)
        {
            //System.out.println("something needs to be removed");
        }
        return parentsExtraChildList;
    }

    // TODO: Implement the method
//    private static WebNode compareVisitedPages(IndexTable indexTable, String url, List<String> res, int parent) throws Exception {
//        int id = indexTable.getIdFromUrl(url);
//        WebNode recordedNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), id, WebNode.class);
//        String recordedDate = recordedNode.getLastModifiedDate();
//        String newDate = LastModifiedDateExtractor.extractModifiedDate(url);
//        // TODO: If the current page is newer than the recorded page, update the page
//        if (LastModifiedDateExtractor.getDateInMilliseconds(recordedDate) < LastModifiedDateExtractor.getDateInMilliseconds(newDate)) {
//            // 1. Create a new node with the same ID because the URL is the same, however, the last modified date, parent, children maybe different.
//            WebNode newNode = new WebNode(id, parent, url, newDate);
//            // 2. Update the id2WebNode tree
//            indexTable.updateEntry(TreeNames.id2WebNode.toString(), id, newNode);
//            // 3. Add it into the result list for indexing.
//            res.add(url);
//            // 4. Update the parent node to add the new node as a child
//            // also need to remove old node from the old parent's children list
//            indexTable.getEntry(TreeNames.id2WebNode.toString(), parent, WebNode.class).addChild(id);
//            // debug:
////            System.out.println("Updated the page with URL: " + url);
////            System.out.println("Old last modified date: " + recordedDate);
////            System.out.println("New last modified date: " + newDate);
////            System.out.printf("The pageID is %d\n", id);
//        }
//        // 5. Update the parent node to be newNode
//        return indexTable.getEntry(TreeNames.id2WebNode.toString(), id, WebNode.class);
//    }
}


//        for (int i = 0; i < URL_array.length; i++) {
//            //System.out.println(URL_array[i]);
//            String stringURL = URL_array[i].toExternalForm();
//            if (!webVec.contains(stringURL)) {
//                //System.out.println("With URL " + link + "found new URL " + stringURL);
//                TreeNode child = new TreeNode(stringURL);
//                parent.addChild(child);
//                nodeList.add(child);
//                webVec.add(stringURL);
//            } else {
//                //System.out.println("With URL " + link + "meet old URL " + stringURL);
//            }
//            return new ArrayList<>();
//        }

//        import java.io.BufferedWriter;
//        import java.io.FileWriter;
//        import java.io.IOException;
//        import java.net.URL;
//        import java.util.ArrayList;
//        import java.util.List;
//        import java.util.Vector;
//        import org.htmlparser.beans.LinkBean;
//        import org.htmlparser.util.ParserException;
//        import javax.swing.tree.TreeNode;
//
//
///**
// * src.src.linkExtractor.LinkExtractor extracts all the links from the given webpage
// @ -23,99 +18,134 @@ import javax.swing.tree.TreeNode;
// */
//
//
//public class LinkExtractor
//{
//    private static Vector<String> webVec;
//    private static List<TreeNode> nodeList;
//    private static int iteration;
//    private String link = "";
//    public LinkExtractor(String url){
//        link = url;
//    }
//
//    public void extractLinks(TreeNode parent) throws ParserException
//    {
//        // extract links in url and return them
//        // ADD YOUR CODES HERE
//        //System.out.println("run extract links ");
//        LinkBean lb = new LinkBean();
//        lb.setURL(link);
//        URL[] URL_array = lb.getLinks();
//        for(int i=0; i<URL_array.length; i++){
//            //System.out.println(URL_array[i]);
//            String stringURL = URL_array[i].toExternalForm();
//            if(!webVec.contains(stringURL))
//            {
//                //System.out.println("With URL " + link + "found new URL " + stringURL);
//                TreeNode child = new TreeNode(stringURL);
//                parent.addChild(child);
//                nodeList.add(child);
//                webVec.add(stringURL);
//            }
//            else
//            {
//                //System.out.println("With URL " + link + "meet old URL " + stringURL);
//            }
//        }
//    }
//
//    public static void writeVectorToFile(Vector<String> vector, String fileName) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
//            for (String element : vector) {
//                writer.write(element);
//                writer.newLine();
//            }
//            System.out.println("Vector elements successfully written to the file.");
//        } catch (IOException e) {
//            System.out.println("An error occurred while writing the file: " + e.getMessage());
//        }
//    }
//
//    public static void main (String[] args) throws ParserException
//    {
//        //String url = "http://www.cs.ust.hk/";
//        //String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
//        //String url = "https://comp4321-hkust.github.io/testpages/testpage.htm";
//        webVec = new Vector<>();
//        webVec.add("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
//        String url = webVec.firstElement();
//        LinkExtractor extractor = new LinkExtractor(url);
//        TreeNode root = new TreeNode(url);
//        nodeList = new ArrayList<>();
//        nodeList.add(root);
//        extractor.extractLinks(root);
//        iteration = 1;
//        while(webVec.size() > iteration)
//        {
//
//            url = webVec.get(iteration);
//            //System.out.print(iteration +"th iteration with URL " + url);
//            extractor = new LinkExtractor(url);
//            extractor.extractLinks(nodeList.get(iteration));
//            iteration = iteration + 1;
//        }
//        iteration = 0;
//
//        /**print the elements on the terminal
//         for (String element : webVec) {
//         System.out.println(element);
//         }
//         */
//
//        writeVectorToFile(webVec, "webpagesExtracted");
//        Vector<String> nodeVec = new Vector<>();
//        for(int showNo=0; showNo < 30; showNo++)
//        {
//            //nodeVec.add("ITEM " + String.valueOf(showNo));
//            nodeVec.add("PAGE TITLE: " + TitleExtractor.extractTitle(nodeList.get(showNo).getData()));
//            nodeVec.add("URL: " + nodeList.get(showNo).getData());
//            for(int i=0; i < nodeList.get(showNo).getChildren().size(); i++)
//            {
//                nodeVec.add("CHILD LINK: " + nodeList.get(showNo).getChildren().get(i).getData());
//            }
//            nodeVec.add("------------------------------------------------------------------------------");
//        }
//
//        writeVectorToFile(nodeVec, "webpagesParentChild");
//    }
//}