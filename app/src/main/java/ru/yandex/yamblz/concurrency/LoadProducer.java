package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.Set;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        super.run();

        final String result = new DownloadLatch().doWork();
        results.add(result);

        ContentFragment.LATCH.countDown();

        onResult.run();
    }
}
