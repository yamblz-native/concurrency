package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer implements Runnable {

    @NonNull
    private final Set<String> results;
    @NonNull
    private final Runnable onResult;


    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
    }

    @Override
    public void run() {

        Log.d("Producer", "Producer starts working");

        final String result = new DownloadLatch().doWork();
        results.add(result);
        Log.d("Producer", "Producer finished working, results size = " + results.size());


        onResult.run();
    }
}
