package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull
    private final Set<String> results;
    @NonNull
    private final Runnable onResult;
    private Exchanger<String> exchanger;


    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult, Exchanger<String> exchanger) {
        this.results = resultSet;
        this.onResult = onResult;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {

        Log.d("Producer", "Producer starts working");

        /* Synchronize via concurrent mechanics */

        final String result = new DownloadLatch().doWork();
        results.add(result);

        Log.d("Producer", "Producer finished working, results size = " + results.size());
        onResult.run();

        try {
            exchanger.exchange(null, 10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
