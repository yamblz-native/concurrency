package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.locks.Condition;
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
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, lock, condition).start();
        }

        new Consumer(params.postFinish, params.dataResults, lock, condition).start();
    }


    private static final class Producer extends LoadProducer {
        final Lock lock;
        final Condition condition;

        public Producer(Set<String> resultSet, Runnable onResult, Lock lock, Condition condition) {
            super(resultSet, onResult);
            this.lock = lock;
            this.condition = condition;
        }

        @Override
        public void synchronize() {
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }


    private static final class Consumer extends PostConsumer {
        final Set<String> dataResults;
        final Lock lock;
        final Condition condition;

        public Consumer(Runnable onFinish, Set<String> dataResults, Lock lock, Condition condition) {
            super(onFinish);
            this.dataResults = dataResults;
            this.lock = lock;
            this.condition = condition;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            lock.lock();
            try {
                while (dataResults.size() < PRODUCERS_COUNT) {
                    condition.await();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
