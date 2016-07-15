package ru.yandex.yamblz.concurrency;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.concurrent.Phaser;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    private Phaser phaser;

    public PostConsumer(@NonNull Runnable onFinish, Phaser phaser) {
        this.onFinish = onFinish;
        this.phaser = phaser;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        super.run();

        /* Synchronize via concurrent mechanics */
        phaser.arriveAndAwaitAdvance();
        onFinish.run();
    }
}
