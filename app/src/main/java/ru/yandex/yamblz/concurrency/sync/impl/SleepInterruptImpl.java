package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class SleepInterruptImpl extends Synchronizer {

    public SleepInterruptImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {
        Consumer consumer = new Consumer(params.postFinish, params.dataResults);
        consumer.start();

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, consumer).start();
        }
    }


    private static final class Producer extends LoadProducer {
        private Consumer consumer;

        public Producer(Set<String> resultSet, Runnable onResult, Consumer consumer) {
            super(resultSet, onResult);
            this.consumer = consumer;
        }

        @Override
        public void synchronize() {
            consumer.interrupt();
        }
    }


    private static final class Consumer extends PostConsumer {
        final Set<String> resultSet;

        public Consumer(Runnable onFinish, Set<String> resultSet) {
            super(onFinish);
            this.resultSet = resultSet;
        }

        @Override
        protected void synchronize() {
            while (resultSet.size() < PRODUCERS_COUNT) {
                try {
                    sleep(1_000_000);
                } catch (InterruptedException e) {
                    // *Yawn* What is the year now?
                }
            }
        }
    }
}
