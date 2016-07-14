package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    @NonNull private final CountDownLatch synchronizer;

    public PostConsumer(@NonNull Runnable onFinish, @NonNull CountDownLatch synchronizer) {
        this.onFinish = onFinish;
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        super.run();

        try {
            synchronizer.await();
            // wait for showing five
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onFinish.run();
    }
}
