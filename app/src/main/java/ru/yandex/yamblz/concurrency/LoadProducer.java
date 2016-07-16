package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {
    private static final String TAG = LoadProducer.class.getSimpleName();

    @NonNull
    private final Set<String> results;
    @NonNull
    private final Runnable onResult;
    @NonNull
    private final CyclicBarrier cyclicBarrier;

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

        /* Checking if we were interrupted while doing work */
        if (isInterrupted()) {
            Log.d(TAG, "Producer was successfully interrupted while doing work");
            return;
        }

        results.add(result);

        /* Using handler just for demonstration (better to use unOnUiThreadIfFragmentAlive)
        * Yes, I know that if we don't interrupt this thread, then app exception will happen */
        new Handler(Looper.getMainLooper()).post(onResult);

        try {
            /* Waiting for other threads */
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Log.d(TAG, "Producer thread was successfully interrupted while waiting others");
        }
    }
}
