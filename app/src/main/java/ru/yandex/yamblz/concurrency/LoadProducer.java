package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.Set;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    private final WaitNotifyLock locker;
    @NonNull private final Runnable onResult;

    public LoadProducer(@NonNull Set<String> resultSet, WaitNotifyLock locker, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.locker = locker;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        super.run();

        synchronized (locker) {
            final String result = new DownloadLatch().doWork();
            results.add(result);
            locker.tick();
            locker.notify();

        }


        onResult.run();
    }
}
