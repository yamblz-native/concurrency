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
    private static String LOG_TAG = "PostConsumer";

    public PostConsumer(@NonNull Runnable onFinish, List<LoadProducer> producerList) {
        this.onFinish = onFinish;
        this.producerList = producerList;
    }

    private void waitForAllTasks() {
        for (LoadProducer producer : producerList) {
            try {
                producer.join();
                Log.i(LOG_TAG, "Thread " + String.valueOf(producer.getId()) + " finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        waitForAllTasks();
        onFinish.run();
    }
}
