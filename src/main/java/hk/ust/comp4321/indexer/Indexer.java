package hk.ust.comp4321.indexer;

import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/***
 * The class to index the title and body of a webpage
 */
public class Indexer implements Runnable {
    private static ForwardInvertedIndex forwardInvertedIndex; // forward inverted index
    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();
    List<HashMap<Integer, String[]>> preprocessedData;

    public static IndexTable indexTable;


    public Indexer(ForwardInvertedIndex forwardInvertedIndex) {
        Indexer.forwardInvertedIndex = forwardInvertedIndex;
    }

    public void setProcessedData(List<HashMap<Integer, String[]>> preprocessedData) {
        this.preprocessedData = preprocessedData;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long startTime = System.currentTimeMillis();
        for (Map.Entry<Integer, String[]> entry : preprocessedData.get(0).entrySet()) {
            executorService.execute(() -> {
                try {
                    lock1.lock();
                    indexTitle(entry.getKey(), entry.getValue());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    lock1.unlock();
                }
            });
        }
        for (Map.Entry<Integer, String[]> entry : preprocessedData.get(1).entrySet()) {
            executorService.execute(() -> {
                try {
                    lock2.lock();
                    indexBody(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    lock2.unlock();
                }
            });
        }
        long endTime = System.currentTimeMillis();
        try {
            executorService.shutdown();
            System.out.println("Title indexing completed");
            boolean terminated = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("Indexing took " + (endTime - startTime) + "ms");
            System.out.println("Title indexing completed");
            System.out.println("Body indexing completed");
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }
        if (!executorService.isTerminated()) {
            System.err.println("Indexing took too long, terminating executor service.");
            executorService.shutdownNow();
        }
    }

    private void indexTitle(Integer pageId, String[] words) throws Exception {
        if (words.length == 0) return;
        int pos = 0; // position of the word in the title
        Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdTitle -> positions
        for (String word : words) {
            if (forwardInvertedIndex.getWordIdTitleFromStem(word) == -1) {
                forwardInvertedIndex.addEntryToWord2IdTitle(word, forwardInvertedIndex.getWordIdTitle());
                forwardInvertedIndex.addEntryToIdTitle2Word(forwardInvertedIndex.getWordIdTitleFromStem(word), word);
            }
            int wordIdTitle = forwardInvertedIndex.getWordIdTitleFromStem(word);
            ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdTitle, k -> new ArrayList<>());
            tmpPos.add(pos);
            pos++;
        }
        for (int wordIdTitle : wordId2Pos.keySet()) {
//            forwardInvertedIndex.updateInvertedIdxTitle(wordIdTitle, pageId, wordId2Pos.get(wordIdTitle).size(), wordId2Pos.get(wordIdTitle));
            forwardInvertedIndex.updateInvertedIdxTitle(wordIdTitle, pageId, wordId2Pos.get(wordIdTitle));
        }
        for (int wordIdTitle : wordId2Pos.keySet()) {
//            forwardInvertedIndex.updateForwardIdxTitle(pageId, wordIdTitle, wordId2Pos.get(wordIdTitle).size(), wordId2Pos.get(wordIdTitle))
//            ;
            forwardInvertedIndex.updateForwardIdxTitle(pageId, wordIdTitle, wordId2Pos.get(wordIdTitle).size());
        }
    }

    private void indexBody(Integer pageId, String[] words) throws Exception {
        int pos = 0; // position of the word in the body
        Hashtable<Integer, ArrayList<Integer>> wordId2Pos = new Hashtable<>(); // wordIdBody -> positions
        for (String word : words) {
            if (forwardInvertedIndex.getWordIdBodyFromStem(word) == -1) {
                forwardInvertedIndex.addEntryToWord2IdBody(word, forwardInvertedIndex.getWordIdBody());
                forwardInvertedIndex.addEntryToIdBody2Word(forwardInvertedIndex.getWordIdBodyFromStem(word), word);
            }
            int wordIdBody = forwardInvertedIndex.getWordIdBodyFromStem(word);
            ArrayList<Integer> tmpPos = wordId2Pos.computeIfAbsent(wordIdBody, k -> new ArrayList<>());
            tmpPos.add(pos);
            pos++;
        }
        for (int wordIdBody : wordId2Pos.keySet()) {
//            forwardInvertedIndex.updateInvertedIdxBody(wordIdBody, pageId, wordId2Pos.get(wordIdBody).size(), wordId2Pos.get(wordIdBody));
            forwardInvertedIndex.updateInvertedIdxBody(wordIdBody, pageId, wordId2Pos.get(wordIdBody));
        }
        for (int wordIdBody : wordId2Pos.keySet()) {
//            forwardInvertedIndex.updateForwardIdxBody(pageId, wordIdBody, wordId2Pos.get(wordIdBody).size(), wordId2Pos.get(wordIdBody));
            forwardInvertedIndex.updateForwardIdxBody(pageId, wordIdBody, wordId2Pos.get(wordIdBody).size());
        }
    }
}