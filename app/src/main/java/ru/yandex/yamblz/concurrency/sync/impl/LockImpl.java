package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class LockImpl extends Synchronizer {

    public LockImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {
        Lock[] locks = new ReentrantLock[PRODUCERS_COUNT];

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            locks[i] = new ReentrantLock();
            new Producer(params.dataResults, params.postResult, locks[i]).start();
        }

        new Consumer(params.postFinish, locks).start();
    }


    private static final class Producer extends LoadProducer {
        final Lock lock;

        public Producer(Set<String> resultSet, Runnable onResult, Lock lock) {
            super(resultSet, onResult);
            this.lock = lock;
        }

        @Override
        protected void acquire() {

            // Race condition. In current implementation it is possible that
            // Consumer would acquire the lock before Producer will do so.
            // Should be accomplished by using another technique or
            // via workflow refactoring.
            lock.lock();
        }

        @Override
        public void synchronize() throws InterruptedException {
            lock.unlock();
        }
    }


    private static final class Consumer extends PostConsumer {
        private final Lock[] locks;

        public Consumer(Runnable onFinish, Lock[] locks) {
            super(onFinish);
            this.locks = locks;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            for (Lock lock : locks) {
                lock.lock();
            }
        }
    }
}
