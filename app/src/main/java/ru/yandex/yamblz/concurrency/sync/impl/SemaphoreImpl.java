package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.Semaphore;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class SemaphoreImpl extends Synchronizer {

    public SemaphoreImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() throws InterruptedException {
        Semaphore semaphore = new Semaphore(PRODUCERS_COUNT);

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            semaphore.acquire();
            new Producer(params.dataResults, params.postResult, semaphore).start();
        }

        new Consumer(params.postFinish, semaphore).start();
    }


    private static final class Producer extends LoadProducer {
        private final Semaphore semaphore;

        public Producer(Set<String> resultSet, Runnable onResult, Semaphore semaphore) {
            super(resultSet, onResult);
            this.semaphore = semaphore;
        }

        @Override
        public void synchronize() {
            semaphore.release();
        }
    }


    private static final class Consumer extends PostConsumer {
        private final Semaphore semaphore;

        public Consumer(Runnable onFinish, Semaphore semaphore) {
            super(onFinish);
            this.semaphore = semaphore;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            semaphore.acquire(PRODUCERS_COUNT);
        }
    }
}
