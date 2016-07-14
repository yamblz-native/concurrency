package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull
    private final Runnable onFinish;
    @NonNull
    private final CountDownLatch latch;

    public PostConsumer(@NonNull Runnable onFinish, @NonNull CountDownLatch latch) {
        this.onFinish = onFinish;
        this.latch = latch;
    }

    @Override
    public void run() {
        waitLatch();

        super.run();
        onFinish.run();
    }

    private void waitLatch() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
