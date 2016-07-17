package ru.yandex.yamblz.concurrency.sync.impl;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class JoinImpl extends Synchronizer {

    public JoinImpl(SyncParameters params) {
        super(params);
    }

    @Override
    protected void customSync() {
        LoadProducer[] producers = new LoadProducer[PRODUCERS_COUNT];

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            producers[i] = new LoadProducer(params.dataResults, params.postResult);
            producers[i].start();
        }

        new Consumer(params.postFinish, producers).start();
    }


    private static final class Consumer extends PostConsumer {
        LoadProducer[] producers;

        public Consumer(Runnable onFinish, LoadProducer[] producers) {
            super(onFinish);
            this.producers = producers;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            for (LoadProducer producer : producers) {
                producer.join();
            }
        }
    }
}
