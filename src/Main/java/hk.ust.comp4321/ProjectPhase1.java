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

        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        stopStem = new StopStem("resources/stopwords.txt");
        try {
            db1 = new IndexTable("CrawlerDatabase11");
            db2 = new ForwardInvertedIndex("ForwardInvertedIndexDatabase11");
            indexer = new Indexer(db1, db2);
            crawler1 = new Crawler(db1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int numPages = 30;
        int numKeywords = 10;
        int numChildLinks = 10;

        List<String> result = crawler1.extractLinks(root, numPages);
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
            keyword2Freq = db2.getKeywordFrequency(pageId, numKeywords, 0);
            keyword2Freq.forEach((k, v) -> System.out.print(k + " " + v + "; "));
            System.out.println(title);

            System.out.println(currentUrl);

            System.out.println(lastModified + " " + pageSize);

            System.out.println();
            int countChildLinks = 0;
            for (String child : currentWebnode.getChildren()) {
                if (countChildLinks >= numChildLinks) {
                    break;
                }
                System.out.println("Child Link: " + child);
                //writer.write(child + "\n");
                countChildLinks++;
            }

            int countParentLinks = 0;
            for (String parent : currentWebnode.getParent()) {
                if (countParentLinks >= 30) {
                    break;
                }
                System.out.println("Parent Link: " + parent);
                countParentLinks++;
            }
            System.out.println("----------------------------------------------------------------------------------------");
        }
    }
}