package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.CyclicBarrier;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull
    private final Runnable onFinish;
    @NonNull
    private final int loadProducersNumber; // Number of load producers
    @NonNull
    private final WeakReference<ContentFragment> fragment;
    private CyclicBarrier cyclicBarrier;


    public PostConsumer(@NonNull Runnable onFinish,
                        int loadProducersNumber, ContentFragment fragment) {
        this.onFinish = onFinish;
        this.loadProducersNumber = loadProducersNumber;
        this.fragment = new WeakReference<>(fragment);
    }


    @Override
    public void run() {
        super.run();
        /* Synchronize via concurrent mechanics */
        this.cyclicBarrier = new CyclicBarrier(loadProducersNumber,
                () -> fragment.get().runOnUiThreadIfFragmentAlive(onFinish));
    }


    @NonNull
    public CyclicBarrier getCyclicBarrier() {
        // Just for safety reasons
        if (cyclicBarrier == null) {
            String detailMessage = "Run consumer thread before initializing and running producers";
            throw new IllegalStateException(detailMessage);
        }

        return cyclicBarrier;
    }
}
