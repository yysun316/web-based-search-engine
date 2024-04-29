package hk.ust.comp4321.invertedIndex;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class rubbish implements Runnable{

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; ++i)
        {
            executorService.execute(() -> {
                try {
                    System.out.println("Hello");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        long endTime = System.currentTimeMillis();
        try {
            System.out.println("Title indexing completed");
            executorService.shutdown();
//            boolean terminated = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("Indexing took " + (endTime - startTime) + "ms");
            System.out.println("Title indexing completed");
            System.out.println("Body indexing completed");
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdownNow();
        }

    }
}
