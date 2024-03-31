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
import java.util.*;

import static hk.ust.comp4321.utils.writeVectorToFile.writeVectorToFile;

public class ProjectPhase1 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static ForwardInvertedIndex db2;
    private static Indexer indexer;
    private static StopStem stopStem;
    private static Vector<String> webVec;


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
                webVec = new Vector<>();
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
                webVec.add(title);
                //writer.println(title);
                webVec.add("URL: " + currentUrl);
                //writer.println(currentUrl);
                webVec.add(lastModified + " " + pageSize);
                //writer.println(lastModified + " " + pageSize);

                List<String> stems = new ArrayList<>();
                List<Integer> freq = new ArrayList<>();
                keyword2Freq.forEach((k, v) -> freq.add(v));
                keyword2Freq.forEach((k, v) -> stems.add(k));
                List<Integer> indices = new ArrayList<>(freq.size());
                for (int i = 0; i < freq.size(); i++) {
                    indices.add(i);
                }
                Collections.sort(indices, (a, b) -> Integer.compare(freq.get(b), freq.get(a)));
                for (int j = 0; j < Math.min(10, indices.size()); j++) {
//                    writer.print(indices.get(j) + " ");
//                    writer.print(stems.get(indices.get(j)) + " ");
//                    writer.print(freq.get(indices.get(j)) + " | ");
                    webVec.add("Keystem " + stems.get(indices.get(j)) + " has frequency " + freq.get(indices.get(j)));
                }


//                keyword2Freq.forEach((k, v) -> writer.print(" "));//writer.print(k + " " + v + "; "));
//                writer.println();

                webVec.add("Child Links:");
                //writer.println("Child Links:");
                List<String> children = currentWebnode.getChildren();
                List<String> parent = currentWebnode.getParent();
                List<String> children10 = new ArrayList<>();
                List<String> parent10 = new ArrayList<>();
                for (int i = 0; i < Math.min(10, children.size()); i++) {
                    String child = children.get(i);
                    webVec.add(child);
                    children10.add(child);
                }
                //writer.println("Parent Links:");
                webVec.add("Parent Links:");
                for (int i = 0; i < Math.min(10, parent.size()); i++) {
                    String par = parent.get(i);
                    webVec.add(par);
                    parent10.add(par);
                }
                writer.println("Child Links:");
                currentWebnode.getChildren().forEach(writer::println);

                //writer.println("Parent Links:");
                //currentWebnode.getParent().forEach(writer::println);

                webVec.add("--------------------------------------------------------------------------------------------------------");
                writer.println("----------------------------------------------------------------------------------------");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeVectorToFile(webVec, "webpagesExtracted");
    }

}