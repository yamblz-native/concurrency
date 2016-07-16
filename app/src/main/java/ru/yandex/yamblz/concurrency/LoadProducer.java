package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.Set;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull
    private final Set<String> results;
    private final WaitNotifyLock lock;
    @NonNull
    private final Runnable onResult;

    public LoadProducer(@NonNull Set<String> resultSet, WaitNotifyLock lock, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.lock = lock;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        super.run();

        synchronized (lock) {
            final String result = new DownloadLatch().doWork();
            results.add(result);
            lock.tick();
            lock.notify();
        }


        onResult.run();
    }
}
