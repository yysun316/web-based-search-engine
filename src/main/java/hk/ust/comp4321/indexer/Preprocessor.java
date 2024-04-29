package hk.ust.comp4321.indexer;

import hk.ust.comp4321.extractors.StringExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Preprocessor implements Callable<List<HashMap<Integer, String[]>>> {
    private List<String> urls;
    private IndexTable indexTable;
    private StopStem stopStem = new StopStem("resources/stopwords.txt");

    public Preprocessor(List<String> urls, IndexTable indexTable) {
        this.urls = urls;
        this.indexTable = indexTable;
    }

    @Override
    public List<HashMap<Integer, String[]>> call() throws Exception {
        ExecutorService executorService1 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExecutorService executorService2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<HashMap<Integer, String[]>>> futureTitle = new ArrayList<>();
        List<Future<HashMap<Integer, String[]>>> futureBody = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        for (String url : urls) {
            futureBody.add(executorService2.submit(() -> processBody(url)));
            futureTitle.add(executorService1.submit(() -> processTitle(url)));
        }

        executorService1.shutdown();
        executorService2.shutdown();
        boolean terminated1 = executorService1.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        boolean terminated2 = executorService1.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        if (!terminated1) {
            System.err.println("Preprocessing took too long, terminating executor service.");
            executorService1.shutdownNow();
        }
        if (!terminated2){
            System.err.println("Preprocessing took too long, terminating executor service.");
            executorService2.shutdownNow();
        }
        HashMap<Integer, String[]> title = new HashMap<>();
        HashMap<Integer, String[]> body = new HashMap<>();
        for (Future<HashMap<Integer, String[]>> future : futureTitle) {
            title.putAll(future.get());
        }
        for (Future<HashMap<Integer, String[]>> future : futureBody) {
            body.putAll(future.get());
        }
        System.out.println(title.size() + " pages preprocessed");
//        System.out.println(title.get(0).length + " words in the first page");
//        System.out.println(title.get(1).length + " words in the second page");
//        System.out.println("First pageid is " + title.keySet().toArray()[0]);
        long endTime = System.currentTimeMillis();
        System.out.println("Preprocessing took " + (endTime - startTime) + "ms");

        List<HashMap<Integer, String[]>> result = new ArrayList<>();
        result.add(title);
        result.add(body);
        return result;
    }

    public HashMap<Integer, String[]> processTitle(String url) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
        if (pageId == -1) throw new Exception("Page ID not found");
        String[] words = Arrays.stream(TitleExtractor.extractTitle(url)
                        .split("[\\s\\p{Punct}&&[^-]]+"))
                .filter(word -> !word.trim().isEmpty())
                .map(String::toLowerCase)
                .filter(word -> !stopStem.isStopWord(word))
                .map(stopStem::stem)
                .toArray(String[]::new);

        HashMap<Integer, String[]> result = new HashMap<>();
        result.put(pageId, words);
        return result;
    }

    public HashMap<Integer, String[]> processBody(String url) throws Exception {
        int pageId = indexTable.getPageIdFromURL(url);
        if (pageId == -1) throw new Exception("Page ID not found");

        String[] words = Arrays.stream(StringExtractor.extractStrings(false, url))
                .map(String::toLowerCase)
                .filter(word -> !stopStem.isStopWord(word))
                .map(stopStem::stem)
                .filter(word -> !word.trim().isEmpty())
                .toArray(String[]::new);

        HashMap<Integer, String[]> result = new HashMap<>();
        result.put(pageId, words);
        return result;
    }
}
