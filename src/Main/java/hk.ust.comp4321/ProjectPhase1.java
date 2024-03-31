package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.extractors.PageSizeExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProjectPhase1 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static ForwardInvertedIndex db2;
    private static Indexer indexer;
    private static StopStem stopStem;


    public static void main(String[] args) throws Exception {
        try {
            try (FileWriter writer = new FileWriter("spider_result.txt")) {

                String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
                stopStem = new StopStem("resources/stopwords.txt");
                try {
                    db1 = new IndexTable("crawlerEmTest");
                    db2 = new ForwardInvertedIndex("whatever");
                    indexer = new Indexer(db1, db2);
                    crawler1 = new Crawler(db1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int numPages = 30;
                int numKeywords = 10;
                int numChildLinks = 10;

                List<String> result = crawler1.extractLinks(root, numPages);
                Integer testId;
                WebNode testWebnode;
                Integer count;
                Integer currentId;
                WebNode currentWebnode;
                String title;
                String lastModified;
                Map<String, Integer> keyword2Freq;
                int pageId;
                int pageSize;
                for (String currentUrl : result) {
                    indexer.index(currentUrl);
                    title = TitleExtractor.extractTitle(currentUrl);
                    pageId = db1.getEntry(TreeNames.url2Id.toString(), currentUrl, Integer.class);
                    currentWebnode = db1.getEntry(TreeNames.id2WebNode.toString(), pageId, WebNode.class);
                    lastModified = currentWebnode.getLastModifiedDate();
                    pageSize = PageSizeExtractor.extractPageSize(currentUrl);
                    keyword2Freq = db2.getKeywordFrequency(pageId, numKeywords);
//                    System.out.println(db2.getWordFromIdBody(103));
//                    System.out.println(title);
                    writer.write(title + "\n");

//                    System.out.println(currentUrl);
                    writer.write(currentUrl + "\n");

//                    System.out.println(lastModified + " " + pageSize);
                    writer.write(lastModified + " " + pageSize + "\n");

                    keyword2Freq.forEach((k, v) -> {
//                        System.out.print(k + " " + v + "; ");
                        try {
                            writer.write(k + " " + v + "; ");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });


//                    System.out.println();
                    writer.write("\n");
                    int countChildLinks = 0;
                    for (String child : currentWebnode.getChildren()) {
                        if (countChildLinks >= numChildLinks) {
                            break;
                        }
//                        System.out.println(child);
                        writer.write(child + "\n");
                        countChildLinks++;
                    }

//                    currentWebnode.getChildren().forEach(child -> {
//
//                        System.out.println(child);
//                        try {
//                            writer.write(child + "\n");
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });

//                    System.out.println("----------------------------------------------------------------------------------------");
                    writer.write("----------------------------------------------------------------------------------------" + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}