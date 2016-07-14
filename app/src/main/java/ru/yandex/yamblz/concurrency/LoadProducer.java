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
    @NonNull private final CountDownLatch threadsCount;

    public LoadProducer( @NonNull Set<String> resultSet, @NonNull Runnable onResult, @NonNull CountDownLatch threadsCount ) {
        this.results = resultSet;
        this.onResult = onResult;
        this.threadsCount = threadsCount;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        final String result = new DownloadLatch().doWork();
        results.add( result );
        threadsCount.countDown();
        onResult.run();
    }

}
