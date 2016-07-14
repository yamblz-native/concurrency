package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final CountDownLatch loadingDone;

    @NonNull private final Runnable onFinish;

    public PostConsumer(@NonNull CountDownLatch loadingDone, @NonNull Runnable onFinish) {
        this.loadingDone = loadingDone;

        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        try {
            loadingDone.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onFinish.run();
    }
}
