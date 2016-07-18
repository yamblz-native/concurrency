package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    private static String LOG_TAG = "LoadProducer";

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        super.run();
        Log.i(LOG_TAG, "Thread " + String.valueOf(this.getId()) + " is running");

        /* Synchronize via concurrent mechanics */

        final String result = new DownloadLatch().doWork();
        synchronized (this.results) {
            results.add(result);
            onResult.run();
        }
    }
}
