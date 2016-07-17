package ru.yandex.yamblz.concurrency;

import android.annotation.TargetApi;
import android.os.Build;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        super.run();

        Log.d("Consumer", "Consumer start");

         /* Synchronize via concurrent mechanics */
        ContentFragment.PHASER.register();
        ContentFragment.PHASER.arriveAndAwaitAdvance();
        ContentFragment.PHASER.arriveAndDeregister();

        Log.d("Consumer", "Running onFinish");
        onFinish.run();

    }


}
