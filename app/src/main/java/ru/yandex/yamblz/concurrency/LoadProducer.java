package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;

/**
 * Simple load producer thread; non-extensible. Oops!
 *
 * @author archinamon on 13/07/16.
 */

public class LoadProducer extends Thread {
    private static final String TAG = LoadProducer.class.getSimpleName();

    @NonNull protected final Set<String> results;
    @NonNull private final Runnable onResult;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
    }


    @Override
    public void run() {
        super.run();

        results.add(new DownloadLatch().doWork());

        onResult.run();

        try {
            synchronize();
        } catch (InterruptedException | BrokenBarrierException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    protected void synchronize() throws InterruptedException, BrokenBarrierException {
        // Empty or overridden
    }
}
