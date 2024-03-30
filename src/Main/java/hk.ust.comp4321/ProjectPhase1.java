package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.extractors.CharacterCounter;
import hk.ust.comp4321.indexer.EmIndex;
import hk.ust.comp4321.indexer.EmIndexContainer;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import jdbm.btree.BTree;

import static hk.ust.comp4321.extractors.StringExtractor.extractStrings;

public class ProjectPhase1 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static Indexer indexer;
    private static StopStem stopStem;


    public static void main(String[] args) throws Exception {
        EmIndexContainer EmIndexContainer = new EmIndexContainer();
        List<String> stringList = List.of("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/news.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/books.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse/PG.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/ust_cse/UG.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/news/bbc.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/news/cnn.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/books/book1.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/books/book2.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/books/book3.htm", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/1.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/2.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/3.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/4.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/5.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/6.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/7.html", "https://www.cse.ust.hk/~kwtleung/COMP4321/Movie/8.html");
        String testUrl;
        Vector<String> nodeVec = new Vector<>();
        String root = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
        stopStem = new StopStem("resources/stopwords.txt");
        try {
            db1 = new IndexTable("crawlerEmTest");
            indexer = new Indexer(db1);
            crawler1 = new Crawler(db1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int numPages = 20;

        List<String> result = crawler1.extractLinks(root, numPages);
        for (String currentUrl : result) {
            Integer count;
            Integer currentId;
            WebNode currentWebnode;
            try {
                System.out.println("ID: " + db1.getIdFromUrl(currentUrl));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Title: " + TitleExtractor.extractTitle(currentUrl));
            System.out.println("URL: " + currentUrl);

            try {
                currentId = db1.getIdFromUrl(currentUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                currentWebnode = db1.getEntry(TreeNames.id2WebNode.toString(), currentId, WebNode.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Last modification data : " + currentWebnode.getLastModifiedDate());
            ///////////////////////////////////////////////////////////////////////////////
            URL url = new URL(currentUrl);
            URLConnection connection = url.openConnection();
            int contentLength = connection.getContentLength();
            if (contentLength != -1) {
                System.out.println("Size of page : " + contentLength + " bytes");
            } else {
                System.out.println("Size of page : Unknown");
            }
            CharacterCounter charCounter = new CharacterCounter();
            int charCount = charCounter.countCharacters(currentUrl);
            System.out.println("Webpage Char Count: " + charCount);

            System.out.println("Keyword and frequency:");
            String[] words;
            List<String> stemsList = new ArrayList<>();
            List<Integer> frequency = new ArrayList<>();
            words = extractStrings(false, currentUrl);
            for (int i = 0; i < words.length; i++) {
                if (!stopStem.isStopWord(words[i])) {
                    String curStem = stopStem.stem(words[i]);
                    if(curStem == "")
                        continue;
                    Integer stemIndex = 0;
                    boolean added = false;
                    for (String stemElement : stemsList) {
                        if(stemElement.equals(curStem))
                        {
                            added = true;
                            frequency.set(stemIndex, frequency.get(stemIndex) + 1);
                            break;
                        }
                        stemIndex++;
                    }
                    if(added == false)
                    {
                        stemsList.add(curStem);
                        frequency.add(1);
                    }

                    EmIndexContainer.addEmIndex(curStem, currentId);
                }
            }
            List<Integer> indices = new ArrayList<>(frequency.size());
            for (int i = 0; i < frequency.size(); i++) {
                indices.add(i);
            }

            Collections.sort(indices, (a, b) -> Integer.compare(frequency.get(b), frequency.get(a)));

            for (int j = 0; j < Math.min(10, indices.size()); j++) {
                System.out.print(indices.get(j) + " ");
                System.out.print(stemsList.get(indices.get(j)) + " ");
                System.out.print(frequency.get(indices.get(j)) + "  |  ");
            }
            System.out.println();
            System.out.println("Double check");
            for (int j = 0; j < Math.min(10, indices.size()); j++) {
                System.out.print(indices.get(j) + " ");
                System.out.print(stemsList.get(indices.get(j)) + " ");
                System.out.print(EmIndexContainer.withIdNStemgetFrequency(currentId,stemsList.get(indices.get(j))));
                System.out.print(frequency.get(indices.get(j)) + "  |  ");
            }
            System.out.println();
            count = 1;
            for (String parent : currentWebnode.getParent()) {
                System.out.println("Parent Link" + count + ": " + parent);
                count++;
            }
            count = 1;
            for (String child : currentWebnode.getChildren()) {
                if (count > 10) {
                    break;
                }
                System.out.println("Child Link" + count + ": " + child);
                count++;
            }
            System.out.println("----------------------------------------------------------------------------------------");
            }
        }
    }