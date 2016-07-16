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

    @NonNull
    private final Set<String> results;
    @NonNull
    private final Runnable onResult;
    private final WaitNotifyLock lock;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult, WaitNotifyLock lock) {
        this.results = resultSet;
        this.onResult = onResult;
        this.lock = lock;
    }

    @Override
    public void run() {
        super.run();

        Log.d("Producer", "Producer starts working");

        synchronized (lock) {
            final String result = new DownloadLatch().doWork();
            results.add(result);
            Log.d("Producer", "Producer finished working, results size = " + results.size());
            lock.tick();
            lock.notify();
        }

        onResult.run();
    }
}
