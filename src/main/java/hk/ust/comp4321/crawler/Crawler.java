package hk.ust.comp4321.crawler;

import hk.ust.comp4321.extractors.LinkExtractor;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.io.IOException;
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
}

	
