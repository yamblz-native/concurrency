package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {
    private final static String TAG = PostConsumer.class.getSimpleName();

    @NonNull private final Runnable onFinish;
    @NonNull private final ContentFragment fragment;
    @NonNull private final CyclicBarrier cyclicBarrier;


    public PostConsumer(@NonNull Runnable onFinish,
                        int loadProducersNumber, @NonNull ContentFragment fragment) {
        this.onFinish = onFinish;
        this.fragment = fragment;
        this.cyclicBarrier = new CyclicBarrier(loadProducersNumber + 1);
    }


    @Override
    public void run() {
        super.run();
        /* Synchronize via concurrent mechanics */
        try {
            cyclicBarrier.await();
            onFinish.run();
        } catch (InterruptedException | BrokenBarrierException e) {
            Log.d(TAG, "Consumer thread was successfully interrupted");
        }

    }

    @NonNull
    public CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }
}
