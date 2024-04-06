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
    private static IndexTable db;

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
}

	
