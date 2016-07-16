package ru.yandex.yamblz.concurrency;

/**
 * Created by user on 16.07.16.
 */
public class WaitNotifyLock {
    private int threads;

    public WaitNotifyLock(int threads) {
        this.threads = threads;
    }


    public int getThreads() {
        return threads;
    }

    public void tick() {
        threads--;
    }
}
