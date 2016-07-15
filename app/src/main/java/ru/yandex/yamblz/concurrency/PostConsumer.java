package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    @NonNull private final CountDownLatch countDown;

    public PostConsumer(@NonNull Runnable onFinish, int producerN) {
        this.onFinish = onFinish;
        countDown = new CountDownLatch(producerN);
    }

    @Override
    public void run() {
        super.run();

        try {
            countDown.await();
        } catch (InterruptedException e) {
            Log.e(PostConsumer.class.toString(), "consumer thread interrupted");
        }

        onFinish.run();
    }

    @NonNull
    public CountDownLatch getCountDownLatch() {
        return countDown;
    }
}
