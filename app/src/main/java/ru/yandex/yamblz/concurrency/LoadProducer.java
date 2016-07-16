package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    @NonNull private final Set<String> results;
    @NonNull private final Runnable onResult;
    private  Condition allStart;
    private ReentrantLock lock;
    private static int PRODUCERS_COUNT;

    public LoadProducer(@NonNull Set<String> resultSet,
                        @NonNull Runnable onResult,
                        ReentrantLock lock,
                        Condition allStart,
                        int count ) {
        this.results = resultSet;
        this.onResult = onResult;
        this.lock =lock;
        this.allStart =allStart;
        this.PRODUCERS_COUNT = count;
    }

    @Override
    public void run() {
        super.run();
        /* Synchronize via concurrent mechanics */

        lock.lock();
        try{
            final String result = new DownloadLatch().doWork();
            results.add(result);
            onResult.run();
            if (results.size()>=PRODUCERS_COUNT)
                allStart.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
