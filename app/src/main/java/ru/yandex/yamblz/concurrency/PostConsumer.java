package ru.yandex.yamblz.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Semaphore;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull
    private final Runnable onFinish;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public PostConsumer(@NonNull Runnable onFinish) {
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        super.run();
        Semaphore semaphore = LoadProducer.getSemaphore();
        while (true) {
            boolean finished = false;
            try {
                semaphore.acquire();
                finished = LoadProducer.getLoadedProducers() == ContentFragment.PRODUCERS_COUNT;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
            if (finished)
                uiHandler.post(onFinish);
            else try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
