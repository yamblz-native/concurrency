package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {
    private final String TAG = this.getClass().getSimpleName();

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    @NonNull private ContentFragment contentFragment;
    @NonNull private final CyclicBarrier cyclicBarrier;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult,
                        @NonNull CyclicBarrier cyclicBarrier, @NonNull ContentFragment contentFragment) {
        this.results = resultSet;
        this.onResult = onResult;
        this.contentFragment = contentFragment;
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        final String result = new DownloadLatch().doWork();
        results.add(result);

        /* Posting result to UI */
        if (!isInterrupted())
            new Handler(Looper.getMainLooper()).post(onResult);

        /* Waiting for other threads */
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            Log.d(TAG, "Producer thread was successfully interrupted");
        }
    }
}
