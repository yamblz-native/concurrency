package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    // Не уверена, что хранить ссылку на один тред в другом - это хорошее решени
    private Thread workingThread;
    @NonNull
    private final Runnable onFinish;

    public PostConsumer(Thread workingThread, @NonNull Runnable onFinish) {
        this.workingThread = workingThread;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        super.run();

        Log.d("Consumer", "Consumer start");
        try {
            workingThread.join();
            Log.d("Consumer", "Consumer join finished; resuming execution");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Consumer","Running onFinish");
        onFinish.run();



    }


}
