package ru.yandex.yamblz.concurrency;

import android.util.Log;

import java.util.Set;

/**
 * Created by user on 16.07.16.
 */
public class ProducersThread extends Thread {

    private final int count;
    private final Set<String> dataResults;
    private final Runnable onResult;

    public ProducersThread(int count, Set<String> dataResults, Runnable onResult) {

        this.count = count;
        this.dataResults = dataResults;
        this.onResult = onResult;
    }
    @Override
    public void run() {
        super.run();
        Log.d("Producers", "Starting producers setup");
        for (int i = 0; i < count; i++) {
            LoadProducer producer = new LoadProducer(dataResults, onResult);
            producer.start();
            try {
                producer.join();
                Log.d("Producers", "Joining process, i = " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Log.d("Producers", "All processes started");

    }
}
