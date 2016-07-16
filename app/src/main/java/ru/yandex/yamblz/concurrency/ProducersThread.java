package ru.yandex.yamblz.concurrency;

import android.util.Log;

import java.util.Set;

/**
 * Created by user on 16.07.16.
 */
public class ProducersThread extends Thread {

    private final WaitNotifyLock lock;
    private final Set<String> dataResults;
    private final Runnable onResult;

    public ProducersThread(WaitNotifyLock lock, Set<String> dataResults, Runnable onResult) {

        this.lock = lock;
        this.dataResults = dataResults;
        this.onResult = onResult;
    }
    @Override
    public void run() {
        super.run();
        Log.d("ProducersManager", "Starting producers setup");

        for (int i = 0; i < lock.getThreads(); i++) {
            new LoadProducer(dataResults, onResult, lock).start();

        }
        Log.d("ProducersManager", "All processes started");

        synchronized (lock) {
            while (lock.getThreads() > 0) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.d("ProducersManager", "Producers manager finished execution");
        }

    }
}
