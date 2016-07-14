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
    @NonNull private final CountDownLatch mCountDownLatch;

    public PostConsumer(@NonNull Runnable onFinish, @NonNull CountDownLatch CountDownLatch) {
        this.onFinish = onFinish;
        this.mCountDownLatch = CountDownLatch;
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        try {
            mCountDownLatch.await(); // Just wait until all threads do their job
            onFinish.run();
        } catch (InterruptedException e) {
            e.printStackTrace(); // We could run onError but we don`t have onError
            Log.w("PostConsumer", "No way! Our consumer thread was interrupted by some very-very bad thread!");
        }
    }
}
