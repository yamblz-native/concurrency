package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    @NonNull private final CyclicBarrier cyclicBarrier;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult,
        @NonNull CyclicBarrier cyclicBarrier) {
        this.results = resultSet;
        this.onResult = onResult;
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */

        final String result = new DownloadLatch().doWork();
        results.add(result);

        // Posting result to UI
        new Handler(Looper.getMainLooper()).post(onResult);

        /* Waiting for other threads */
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
