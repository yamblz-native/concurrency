package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
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
    @NonNull private final CountDownLatch countDownLatch;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult, @NonNull CountDownLatch countDownLatch) {
        this.results = resultSet;
        this.onResult = onResult;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        super.run();
        final String result = new DownloadLatch().doWork();
        results.add(result);
        countDownLatch.countDown();
        new Handler(Looper.getMainLooper()).post(onResult);
    }
}
