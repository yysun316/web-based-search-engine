package hk.ust.comp4321;

import hk.ust.comp4321.crawler.Crawler;
import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.extractors.CharacterCounter;
import hk.ust.comp4321.indexer.Indexer;
import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.Posting;
import hk.ust.comp4321.utils.TreeNames;
import hk.ust.comp4321.utils.WebNode;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ProjectPhase2 {
    private static Crawler crawler1;
    private static IndexTable db1;
    private static Indexer indexer;
    private static StopStem stopStem;


    public static void main(String[] args) throws Exception {

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
            System.out.println("Last modification data : " + LastModifiedDateExtractor.extractModifiedDate(currentUrl));
            ///////////////////////////////////////////////////////////////////////////////
            URL url = new URL(currentUrl);
            URLConnection connection = url.openConnection();
            int contentLength = connection.getContentLength();
            if (contentLength != -1) {
                System.out.println("Size of page : " + contentLength + " bytes");
            } else {
                System.out.println("Size of page : Unknown");
            }
            ///////////////////////////////////////////////////////////////////////////////
            CharacterCounter charCounter = new CharacterCounter();
            int charCount = charCounter.countCharacters(currentUrl);
            System.out.println("Webpage Word Count: " + charCount);
            ///////////////////////////////////////////////////////////////////////////////
            System.out.println("Keyword and frequency");
//            String[] words;
//            List<String> stems = new ArrayList<>();
//            List<String> keywords = new ArrayList<>();
//            int[] frequency = {0,0,0,0,0,0,0,0,0,0};
//            words = extractStrings(false, currentUrl);
////            indexer.index(currentUrl);
//            count = 1;
//            for (int i = 0; i < words.length; i++) {
//                if (!stopStem.isStopWord(words[i])) {
//                    stems.add(stopStem.stem(words[i]));
//                    keywords.add(words[i]);
//                    count++;
//                    if (count >= 10) {
//                        break;
//                    }
//                }
//            }
//            for(int i = 0; i < words.length; i++)
//            {
//                for(int j = 0; j < keywords.size(); j++)
//                {
//                    if(words[i]==keywords.get(j))
//                    {
//                        frequency[j] = frequency[j] + 1;
//                    }
//                }
//            }
//            count = 0;
//            System.out.print("keywords are:");
//            for (String keyword : keywords) {
//                System.out.print(keyword);
//                System.out.print(" " + frequency[count] + "\t");
//                count++;
//            }
//            System.out.println();
//            System.out.print("their stems are:");
//            System.out.println(stems);
            indexer.index(currentUrl);

            Integer id = db1.getEntry(TreeNames.url2Id.toString(), currentUrl, Integer.class);
            System.out.println("id is " + id.toString());
            BTree temp = db1.getForwardIdxBodyEntry(id);
            System.out.println("Btree is : " + temp);
            TupleBrowser browser = temp.browse();
            Tuple tuple = new Tuple();
            while (browser.getNext(tuple)) {
                Object currentPosting = tuple.getKey();
                ArrayList<Integer> value = (ArrayList<Integer>) tuple.getValue();
                System.out.println("Value for key " + currentPosting + ": " + value);
                break;
            }


                ///////////////////////////////////////////////////////////////////////////////
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