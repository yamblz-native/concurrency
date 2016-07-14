package ru.yandex.yamblz.concurrency;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    @NonNull private final Handler uiHandler;
    @NonNull private final CountDownLatch countDownLatch;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult,
                        @NonNull Handler uiHandler, @NonNull CountDownLatch countDownLatch) {
        this.results = resultSet;
        this.onResult = onResult;
        this.uiHandler = uiHandler;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */

        final String result = new DownloadLatch().doWork();

        synchronized (results) {
            results.add(result);
        }

        uiHandler.post(onResult::run);
        countDownLatch.countDown();
    }
}
