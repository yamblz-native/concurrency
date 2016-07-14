package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Simple load producer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class LoadProducer extends Thread {

    private static final int THREADS_AVAILABLE = 1;
    private static final Semaphore semaphore = new Semaphore(THREADS_AVAILABLE, true);
    private static int loadedProducers = 0;

    public static Semaphore getSemaphore() {
        return semaphore;
    }

    @NonNull
    private final Set<String> results;
    @NonNull
    private final Runnable onResult;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public LoadProducer(@NonNull Set<String> resultSet, @NonNull Runnable onResult) {
        this.results = resultSet;
        this.onResult = onResult;
    }

    @Override
    public void run() {
        super.run();

        final String result = new DownloadLatch().doWork();

        try {
            semaphore.acquire();
            loadedProducers++;
            results.add(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }

        uiHandler.post(onResult);
    }

    public static int getLoadedProducers() {
        return loadedProducers;
    }
}
