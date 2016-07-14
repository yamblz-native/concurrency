package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final CountDownLatch countDownLatch;
    @NonNull private final Runnable onFinish;

    public PostConsumer(@NonNull CountDownLatch countDownLatch, @NonNull Runnable onFinish) {
        this.countDownLatch = countDownLatch;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Такого не будет", e);
        }
        onFinish.run();
    }
}
