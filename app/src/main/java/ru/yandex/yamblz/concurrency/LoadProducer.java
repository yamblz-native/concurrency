package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    @NonNull private final CountDownLatch synchronizer;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult,
                        @NonNull CountDownLatch synchronizer) {
        this.results = resultSet;
        this.onResult = onResult;
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */

        final String result = new DownloadLatch().doWork();
        synchronized (results) {
            results.add(result);
        }

        synchronizer.countDown();
        onResult.run();
    }
}
