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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;

public class ProjectPhase1 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static ForwardInvertedIndex db2;
    private static Indexer indexer;
    private static StopStem stopStem;


    public static void main(String[] args) throws Exception {
        try {
            PrintWriter writer = new PrintWriter("spider_result.txt", "UTF-8");

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
            int numPages = 20;

            List<String> result = crawler1.extractLinks(root, numPages);
            Integer testId;
            WebNode testWebnode;
            Integer count;
            Integer currentId;
            WebNode currentWebnode;
            String title;
            String lastModified;
            Hashtable<String, Integer> keyword2Freq = new Hashtable<>();
            int pageId;
            int pageSize;
            for (String currentUrl : result) {
                indexer.index(currentUrl);
                title = TitleExtractor.extractTitle(currentUrl);
                pageId = db1.getEntry(TreeNames.url2Id.toString(), currentUrl, Integer.class);
                currentWebnode = db1.getEntry(TreeNames.id2WebNode.toString(), pageId, WebNode.class);
                lastModified = currentWebnode.getLastModifiedDate();
                pageSize = PageSizeExtractor.extractPageSize(currentUrl);
                keyword2Freq = db2.getKeywordFrequency(pageId);
                writer.println(title);
                writer.println(currentUrl);
                writer.println(lastModified + " " + pageSize);
                keyword2Freq.forEach((k, v) -> writer.print(k + " " + v + "; "));
                writer.println();
                currentWebnode.getChildren().forEach(writer::println);
                writer.println("----------------------------------------------------------------------------------------");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}