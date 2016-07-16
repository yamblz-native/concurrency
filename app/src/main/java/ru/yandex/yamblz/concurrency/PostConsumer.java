package ru.yandex.yamblz.concurrency;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Phaser;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;
    @NonNull private final Phaser phaser;

    public PostConsumer(@NonNull Phaser phaser, @NonNull Runnable onFinish) {
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
