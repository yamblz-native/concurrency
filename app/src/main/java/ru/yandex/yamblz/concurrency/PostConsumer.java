package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    @NonNull private final List<LoadProducer> producerList;
    private static String LOG_TAG = "Yamblz:PostConsumer";

    public PostConsumer(@NonNull Runnable onFinish, List<LoadProducer> producerList) {
        this.onFinish = onFinish;
        this.producerList = producerList;
    }

    private void waitForAllTasks() throws InterruptedException {
        for (LoadProducer producer : producerList) {
            producer.join();
            Log.i(LOG_TAG, "Thread " + String.valueOf(producer.getId()) + " joined");
        }
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        try {
            waitForAllTasks();
        } catch (InterruptedException e) {
            Log.i(LOG_TAG, "PostConsumer was interrupted");
            return;
        }
        onFinish.run();
    }
}
