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
    private final String TAG = this.getClass().getSimpleName();

    @NonNull
    private final Runnable onFinish;
    @NonNull
    private final int loadProducersNumber; // Number of load producers
    @NonNull
    private ContentFragment fragment;
    @NonNull
    private CyclicBarrier cyclicBarrier;


    public PostConsumer(@NonNull Runnable onFinish,
                        int loadProducersNumber, @NonNull ContentFragment fragment) {
        this.onFinish = onFinish;
        this.loadProducersNumber = loadProducersNumber;
        this.fragment = fragment;
        this.cyclicBarrier = new CyclicBarrier(loadProducersNumber + 1);
    }


    @Override
    public void run() {
        super.run();
        /* Synchronize via concurrent mechanics */
        try {
            cyclicBarrier.await();
            fragment.runOnUiThreadIfFragmentAlive(onFinish);
        } catch (InterruptedException | BrokenBarrierException e) {
            Log.d(TAG, "Consumer thread was successfully interrupted");
        }

    }

    @NonNull
    public CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }
}
