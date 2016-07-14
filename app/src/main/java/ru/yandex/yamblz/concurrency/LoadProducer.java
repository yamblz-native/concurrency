package ru.yandex.yamblz.concurrency;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.Phaser;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    @NonNull private final Phaser phaser;

    public LoadProducer(@NonNull Phaser phaser, @NonNull Set<String> resultSet, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
        this.phaser = phaser;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        super.run();

        final String result = new DownloadLatch().doWork();
        results.add(result);

        phaser.arriveAndDeregister();

        onResult.run();
    }
}
