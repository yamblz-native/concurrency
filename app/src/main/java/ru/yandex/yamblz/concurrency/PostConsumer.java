package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    private CyclicBarrier barrier;

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        onFinish.run();
    }

    public PostConsumer(@NonNull Runnable onFinish, CyclicBarrier barrier) {
        this.onFinish = onFinish;
        this.barrier = barrier;
    }
}
