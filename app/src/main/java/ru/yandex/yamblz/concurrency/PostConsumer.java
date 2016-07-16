package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull
    private final Runnable onFinish;
    private ReentrantLock lock;
    private Condition allStart;

    public PostConsumer(@NonNull Runnable onFinish, ReentrantLock lock, Condition allStart) {
        this.onFinish = onFinish;
        this.lock = lock;
        this.allStart = allStart;
    }

    @Override
    public void run() {
        super.run();
        lock.lock();
        try {
            allStart.await();
            onFinish.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        //   onFinish.run();
    }
}
