package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    @NonNull private final CyclicBarrier barrier;

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull CyclicBarrier barrier
            , @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        super.run();
        /* Synchronize via concurrent mechanics */
        final String result = new DownloadLatch().doWork();
        if(Thread.interrupted()){
            Log.d("LoadProducer","interrupted before barrier");
        }
        else{
            synchronized (this){
                results.add(result);
            }

            onResult.run();
            try {
                barrier.await();
            } catch (InterruptedException e) {
                Log.d("LoadProducer","interrupted after barrier");
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
