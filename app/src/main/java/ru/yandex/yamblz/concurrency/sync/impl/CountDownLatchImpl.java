package ru.yandex.yamblz.concurrency.sync.impl;

import android.support.annotation.NonNull;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;
import ru.yandex.yamblz.ui.fragments.ContentFragment;

public class CountDownLatchImpl extends Synchronizer {

    public CountDownLatchImpl(SyncParameters params) {
        super(params);
    }


    @Override
    public void customSync() {
        CountDownLatch countDownLatch = new CountDownLatch(ContentFragment.PRODUCERS_COUNT);

        for (int i = 0; i < ContentFragment.PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, countDownLatch).start();
        }

        new Consumer(params.postFinish, countDownLatch).start();
    }


    private static final class Producer extends LoadProducer {

        @NonNull private final CountDownLatch countDownLatch;

        public Producer(@NonNull Set<String> resultSet, @NonNull Runnable onResult, @NonNull CountDownLatch countDownLatch) {
            super(resultSet, onResult);
            this.countDownLatch = countDownLatch;
        }


        @Override
        public void synchronize() {
            countDownLatch.countDown();
        }
    }


    private static final class Consumer extends PostConsumer {

        @NonNull private final CountDownLatch countDownLatch;

        public Consumer(@NonNull Runnable onFinish, @NonNull CountDownLatch countDownLatch) {
            super(onFinish);
            this.countDownLatch = countDownLatch;
        }

        @Override
        protected void synchronize() {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) { /* */ }
        }
    }
}
