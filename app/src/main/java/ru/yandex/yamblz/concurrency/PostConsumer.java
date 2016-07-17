package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ExecutionException;

/**
 * Simple result consumer thread; non-extensible. Oops!
 *
 * @author archinamon on 13/07/16.
 */

public class PostConsumer extends Thread {
    private static final String TAG = PostConsumer.class.getSimpleName();

    @NonNull private final Runnable onFinish;

    public PostConsumer(@NonNull Runnable onFinish) {
        this.onFinish = onFinish;
    }


    @Override
    public void run() {
        super.run();

        try {
            synchronize();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        onFinish.run();
    }


    protected void synchronize() throws InterruptedException, ExecutionException {
        // Empty or overridden
    }
}
