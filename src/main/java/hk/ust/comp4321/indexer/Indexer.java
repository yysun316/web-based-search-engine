package hk.ust.comp4321.indexer;

import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/***
 * The class to index the title and body of a webpage
 */
public class Indexer extends Thread {
    private static StopStem stopStem; // stop words and stemming
    private static IndexTable indexTable; // index table
    private static ForwardInvertedIndex forwardInvertedIndex; // forward inverted index
    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();
    List<HashMap<Integer, String[]>> preprocessedData;

    /***
     * Constructor of the class
     * @param indexTable the index table (url2Id, id2WebNode)
     * @param forwardInvertedIndex the forward inverted index
     * @param stopPath the path of the stop words file
     */
    public Indexer(IndexTable indexTable, ForwardInvertedIndex forwardInvertedIndex, String stopPath) {
        Indexer.indexTable = indexTable;
        stopStem = new StopStem(stopPath);
        Indexer.forwardInvertedIndex = forwardInvertedIndex;
    }
    public void setProcessedData(List<HashMap<Integer, String[]>> preprocessedData) {
        this.preprocessedData = preprocessedData;
    }

    @Override
    public void run() {
        ExecutorService executorService1 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExecutorService executorService2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long startTime = System.currentTimeMillis();
        for (Map.Entry<Integer, String[]> entry : preprocessedData.get(0).entrySet()) {
            executorService1.submit(() -> {
                try {
                    indexTitle(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            executorService2.submit(() -> {
                try {
                    indexBody(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Indexing took " + (endTime - startTime) + "ms");
        executorService2.shutdown();
        executorService1.shutdown();
        boolean terminated1;
        try {
            terminated1 = executorService1.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        boolean terminated2;
        try {
            terminated2 = executorService1.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!terminated1) {
            System.err.println("Preprocessing took too long, terminating executor service.");
            executorService1.shutdownNow();
        }
        if (!terminated2) {
            System.err.println("Preprocessing took too long, terminating executor service.");
            executorService2.shutdownNow();
        }
    }

//    /***
//     * Index the title and body of a webpage
//     * @param url the URL of the webpage
//     * @throws Exception if the page ID is not found
//     */
//    public void index(String url) throws Exception {
//        indexTitle(url);
//        indexBody(url);
//    }


    private void indexTitle(Integer pageId, String[] words) throws Exception {
        try {
            lock1.lock();
            if (words.length == 0) return;
            int pos = 0; // position of the word in the title
            Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdTitle -> positions
            for (String word : words) {
                // If exists in the word2IdTitle table, get the wordId; otherwise, add it to the tables
                if (forwardInvertedIndex.getWordIdTitleFromStem(word) == -1) {
                    forwardInvertedIndex.addEntry(TreeNames.word2IdTitle.toString(), word, forwardInvertedIndex.getWordIdTitle());
                    forwardInvertedIndex.addEntry(TreeNames.IdTitle2Word.toString(), forwardInvertedIndex.getWordIdTitleFromStem(word), word);
                }
                // Find all the positions of the stem in the title
                int wordIdTitle = forwardInvertedIndex.getWordIdTitleFromStem(word);
                ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdTitle, k -> new ArrayList<>());
                tmpPos.add(pos);
                pos++;
            }
            // 5. Insert the wordIdTitle and positions into the inverted index
            for (int wordIdTitle : wordId2Pos.keySet()) {
                forwardInvertedIndex.updateInvertedIdx(TreeNames.invertedIdxTitle.toString(), wordIdTitle, pageId, wordId2Pos.get(wordIdTitle).size(), wordId2Pos.get(wordIdTitle));
            }
            // 6. Insert the wordIdTitle and id into the forward index
            for (int wordIdTitle : wordId2Pos.keySet()) {
                forwardInvertedIndex.updateForwardIdx(TreeNames.forwardIdxTitle.toString(), pageId, wordIdTitle, wordId2Pos.get(wordIdTitle).size(), wordId2Pos.get(wordIdTitle));
            }
        } finally {
            lock1.unlock();
        }

    }

    private void indexBody(Integer pageId, String[] words) throws Exception {
        try {
            lock2.lock();
            int pos = 0; // position of the word in the body
            Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdBody -> positions
            for (String word : words) {
                // If exists in the word2IdBody table, get the wordId; otherwise, add it to the tables
                if (forwardInvertedIndex.getWordIdBodyFromStem(word) == -1) {
                    forwardInvertedIndex.addEntry(TreeNames.word2IdBody.toString(), word, forwardInvertedIndex.getWordIdBody());
                    forwardInvertedIndex.addEntry(TreeNames.IdBody2Word.toString(), forwardInvertedIndex.getWordIdBodyFromStem(word), word);
                }
                // 4. Find all the positions of the word in the body
                int wordIdBody = forwardInvertedIndex.getWordIdBodyFromStem(word);
                ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdBody, k -> new ArrayList<>());
                tmpPos.add(pos);
                pos++;
            }
            // Insert the wordIdBody and positions into the inverted index
            for (int wordIdBody : wordId2Pos.keySet()) {
                forwardInvertedIndex.updateInvertedIdx(TreeNames.invertedIdxBody.toString(), wordIdBody, pageId, wordId2Pos.get(wordIdBody).size(), wordId2Pos.get(wordIdBody));
            }
            // Insert the wordIdBody and id into the forward index
            for (int wordIdBody : wordId2Pos.keySet()) {
                forwardInvertedIndex.updateForwardIdx(TreeNames.forwardIdxBody.toString(), pageId, wordIdBody, wordId2Pos.get(wordIdBody).size(), wordId2Pos.get(wordIdBody));
            }
        } finally {
            lock2.unlock();
        }
    }

}