package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.Exchanger;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class ExchangerImpl extends Synchronizer {

    public ExchangerImpl(SyncParameters params) {
        super(params);
    }


    @Override
    @SuppressWarnings("unchecked")
    protected void customSync() {

        // Payload is transferred via shared set, so just send nulls
        Exchanger<Void>[] exchangers = (Exchanger<Void>[]) new Exchanger<?>[PRODUCERS_COUNT];

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            exchangers[i] = new Exchanger<>();
            new Producer(params.dataResults, params.postResult, exchangers[i]).start();
        }

        new Consumer(params.postFinish, exchangers).start();
    }


    private static final class Producer extends LoadProducer {
        final Exchanger<Void> exchanger;

        public Producer(Set<String> resultSet, Runnable onResult, Exchanger<Void> exchanger) {
            super(resultSet, onResult);
            this.exchanger = exchanger;
        }

        @Override
        public void synchronize() throws InterruptedException {
            exchanger.exchange(null);
        }
    }


    private static final class Consumer extends PostConsumer {
        private final Exchanger<Void>[] exchangers;

        public Consumer(Runnable onFinish, Exchanger<Void>[] exchangers) {
            super(onFinish);
            this.exchangers = exchangers;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            for (Exchanger<Void> exchanger : exchangers) {
                exchanger.exchange(null);
            }
        }
    }
}
