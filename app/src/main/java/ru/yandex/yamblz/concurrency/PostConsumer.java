package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

/**
 * Simple result consumer thread; non-extensible. Oops!
 *
 * @author archinamon on 13/07/16.
 */

public abstract class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;

    public PostConsumer(@NonNull Runnable onFinish) {
        this.onFinish = onFinish;
    }


    @Override
    public void run() {
        super.run();

        synchronize();

        onFinish.run();
    }


    protected abstract void synchronize();
}
