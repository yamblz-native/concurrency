package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    @NonNull private final Handler uiHandler;
    @NonNull private final CountDownLatch countDownLatch;

    public PostConsumer(@NonNull Runnable onFinish, @NonNull Handler uiHandler,
                        @NonNull CountDownLatch countDownLatch) {
        this.onFinish = onFinish;
        this.uiHandler = uiHandler;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        super.run();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new AssertionError("Something went wrong");
        }

        /* Synchronize via concurrent mechanics */
        uiHandler.post(onFinish::run);
    }
}
