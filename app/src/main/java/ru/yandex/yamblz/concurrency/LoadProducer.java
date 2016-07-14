package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    CyclicBarrier barrier;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult, CyclicBarrier barrier) {
        this.results = resultSet;
        this.onResult = onResult;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        super.run();



        /* Synchronize via concurrent mechanics */


        final String result = new DownloadLatch().doWork();
        synchronized (results) {
            results.add(result);
        }

        onResult.run();
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }


    }
}
