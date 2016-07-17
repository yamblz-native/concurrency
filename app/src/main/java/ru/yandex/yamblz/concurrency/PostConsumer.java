package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull
    private final Runnable onFinish;

    public PostConsumer(@NonNull Runnable onFinish) {
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        super.run();

        Log.d("Consumer", "Consumer start");


        try {
            // Прикольно, не знала, что так можно))
            while (ContentFragment.EXCHANGER.exchange(null).size() < ContentFragment.PRODUCERS_COUNT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("Consumer", "Running onFinish");
        onFinish.run();


    }


}
