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
    private final CountDownLatch threadsCount;

    public PostConsumer( @NonNull Runnable onFinish, CountDownLatch threadsCount ) {
        this.onFinish = onFinish;
        this.threadsCount = threadsCount;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        try {
            threadsCount.await();
            onFinish.run();

        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

    }
}
