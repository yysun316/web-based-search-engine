package hk.ust.comp4321.utils;

import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PageRank {
    public static void PageRank(IndexTable indexTable, String rootURL) throws Exception {
        Double dampingFactor = 0.5;
        List<String> res = new LinkedList<>(); // store the result
        Queue<String> queue = new LinkedList<>();
        queue.add(rootURL);
        while (!queue.isEmpty()) {
            String parentURL = queue.poll();
            res.add(parentURL);
            // get parent webnode
            WebNode parentWebNode;
            int parId = indexTable.getIdFromUrl(parentURL);
            if(parId == -1)
            {
                System.out.println("Error: Em expect the node to exist but it doesn't according to indextable");
                System.out.println("Corresponding problematic node is with URL: " + parentURL);
            }
            parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
            Double parentOriginalPageRank = parentWebNode.getPagerank();
            List<String> ChildrenList = parentWebNode.getChildren();
            Double parentFactor = parentOriginalPageRank / ChildrenList.size();

            // iterate through the list of child url of parent
            // get child webnodes and update pageranks
            for (String childURL : ChildrenList) {
                // if the parent child relationship has been set up previously then we need to do nothing
                WebNode childWebNode;
                int childId = indexTable.getIdFromUrl(childURL);
                childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                Double childOriginalPageRank = childWebNode.getPagerank();
                childWebNode.updatePagerank(childOriginalPageRank + dampingFactor * parentFactor);
                if (!res.contains(childURL)) {
                    queue.add(childURL);
                }
            }
            parentWebNode.updatePagerank(parentOriginalPageRank - dampingFactor);
        }
    }
}
