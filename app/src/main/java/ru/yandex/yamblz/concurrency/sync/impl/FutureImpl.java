package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class FutureImpl extends Synchronizer {

    public FutureImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {
        Future<?>[] futures = new Future<?>[PRODUCERS_COUNT];
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            futures[i] = executorService.submit(new LoadProducer(params.dataResults, params.postResult));
        }

        new Consumer(params.postFinish, futures).start();
    }


    private static final class Consumer extends PostConsumer {
        private Future<?>[] futures;

        public Consumer(Runnable onFinish, Future<?>[] futures) {
            super(onFinish);
            this.futures = futures;
        }

        @Override
        protected void synchronize() throws Exception {
            for (Future<?> future : futures) {
                future.get();
            }
        }
    }
}
