package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull private final Runnable onFinish;

    public PostConsumer(@NonNull Runnable onFinish) {
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        super.run();

        try {
            ContentFragment.cdLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onFinish.run();
    }
}
