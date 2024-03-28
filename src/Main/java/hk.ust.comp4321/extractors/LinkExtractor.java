package hk.ust.comp4321.extractors;// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser

import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.ArrayList;
import java.util.List;

/**
 * src.src.linkExtractor.LinkExtractor extracts all the links from the given webpage
 * and prints them on standard output.
 */


public class LinkExtractor {
    public static List<String> extractLinks(IndexTable indexTable, String rootURL, int numPages) throws Exception {
        List<String> res = new ArrayList<>(numPages); // store the result
//        LinkBean lb = new LinkBean();
//        URL[] URL_array;
//
//        Queue<String> queue = new LinkedList<>();
//        WebNode root;
//        int rootId = indexTable.getIdFromUrl(rootURL);
//        if (rootId != -1)
//            root = indexTable.getEntry(TreeNames.id2WebNode.toString(), rootId, WebNode.class);
//        else
//            root = new WebNode(indexTable.getPageId(), null, rootURL, LastModifiedDateExtractor.extractModifiedDate(rootURL));
//        lb.setURL(rootURL);
//        for (URL url : lb.getLinks()) {
//            String stringURL = url.toExternalForm();
//            queue.add(stringURL);
//        }
//        int parentID = root.getId();
//        while (!queue.isEmpty() && res.size() < numPages) {
//            String url = queue.poll();
//            // Handle Cyclic links
//            if (res.contains(url)) continue;
//            // Compare modified date for visited pages
//            if (indexTable.getIdFromUrl(url) != -1)
//                compareVisitedPages(indexTable, url, res, parentID);
//                // Handle unvisited pages
//            else {
//                res.add(url);
//                WebNode node = new WebNode(indexTable.getPageId(), parentID, url, LastModifiedDateExtractor.extractModifiedDate(url));
//                indexTable.addEntry(TreeNames.url2Id.toString(), url, indexTable.getPageId());
//                indexTable.addEntry(TreeNames.id2WebNode.toString(), node.getId(), node);
//                indexTable.getEntry(TreeNames.id2WebNode.toString(), parentID, WebNode.class).addChild(node.getId());
//            }
//            // BFS the webpages
//            lb.setURL(url);
//            URL_array = lb.getLinks();
//            for (URL u : URL_array) {
//                String stringURL = u.toExternalForm();
//                if (!res.contains(stringURL)) {
//                    queue.add(stringURL);
//                }
//            }
//        }
        return res;
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