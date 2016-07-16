package ru.yandex.yamblz.concurrency;

import android.util.Log;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 17.07.16.
 */
public class Executorer extends Thread {

    private final int PRODUCERS_COUNT;
    private final Set<String> dataResults;
    private final Runnable postResult;
    private final Runnable postFinish;

    public Executorer(int count, Set<String> dataResults, Runnable postResult, Runnable postFinish) {

        this.PRODUCERS_COUNT = count;
        this.dataResults = dataResults;
        this.postResult = postResult;
        this.postFinish = postFinish;
    }

    @Override
    public void run() {
        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(PRODUCERS_COUNT);
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            executorService.execute(new LoadProducer(dataResults, postResult));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Executor", "All producers finished working");
        }
        new PostConsumer(postFinish).start();
    }
}
