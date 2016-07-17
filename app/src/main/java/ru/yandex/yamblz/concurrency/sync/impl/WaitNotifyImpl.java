package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class WaitNotifyImpl extends Synchronizer {

    public WaitNotifyImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult).start();
        }

        new Consumer(params.postFinish, params.dataResults).start();
    }


    private static final class Producer extends LoadProducer {

        public Producer(Set<String> resultSet, Runnable onResult) {
            super(resultSet, onResult);
        }

        @Override
        public void synchronize() {
            synchronized (results) {
                results.notify();
            }
        }
    }


    private static final class Consumer extends PostConsumer {
        final Set<String> resultSet;

        public Consumer(Runnable onFinish, Set<String> resultSet) {
            super(onFinish);
            this.resultSet = resultSet;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            synchronized (resultSet) {
                while (resultSet.size() < PRODUCERS_COUNT) {
                    resultSet.wait();
                }
            }
        }
    }
}
