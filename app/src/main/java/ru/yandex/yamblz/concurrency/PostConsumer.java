package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    @NonNull private final CyclicBarrier barrier;

    public PostConsumer(@NonNull Runnable onFinish, @NonNull CyclicBarrier barrier) {
        this.onFinish = onFinish;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        super.run();
        try {
            barrier.await();
            onFinish.run();
        } catch (InterruptedException e) {
            Log.d("PostConsumer","interrupted");
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        /* Synchronize via concurrent mechanics */


    }
}
