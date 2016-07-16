package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

/**
 * Simple result consumer thread; non-extensible. Oops!
 *
 * @author archinamon on 13/07/16.
 */

public class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;

    public PostConsumer(@NonNull Runnable onFinish) {
        this.onFinish = onFinish;
    }


    @Override
    public void run() {
        super.run();

        try {
            synchronize();
        } catch (Exception ignored) { /* */ }

        onFinish.run();
    }


    protected void synchronize() throws Exception {
        // Empty or overridden
    }
}
