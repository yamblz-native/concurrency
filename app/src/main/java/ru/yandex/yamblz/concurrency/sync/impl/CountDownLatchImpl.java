package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class CountDownLatchImpl extends Synchronizer {

    public CountDownLatchImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {
        CountDownLatch countDownLatch = new CountDownLatch(PRODUCERS_COUNT);

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, countDownLatch).start();
        }

        new Consumer(params.postFinish, countDownLatch).start();
    }


    private static final class Producer extends LoadProducer {
        final CountDownLatch countDownLatch;

        public Producer(Set<String> resultSet, Runnable onResult, CountDownLatch countDownLatch) {
            super(resultSet, onResult);
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void synchronize() {
            countDownLatch.countDown();
        }
    }


    private static final class Consumer extends PostConsumer {
        private final CountDownLatch countDownLatch;

        public Consumer(Runnable onFinish, CountDownLatch countDownLatch) {
            super(onFinish);
            this.countDownLatch = countDownLatch;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            countDownLatch.await();
        }
    }
}
