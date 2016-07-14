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
    @NonNull private final CountDownLatch mCountDownLatch;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult, @NonNull CountDownLatch CountDownLatch) {
        this.results = resultSet;
        this.onResult = onResult;
        this.mCountDownLatch = CountDownLatch;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        final String result = new DownloadLatch().doWork();
        synchronized (results) { // Set<> is not synchronized, it`s bad to add two strings at one moment (I believe it =))
            results.add(result);
            mCountDownLatch.countDown();
        }

        onResult.run();
    }
}
