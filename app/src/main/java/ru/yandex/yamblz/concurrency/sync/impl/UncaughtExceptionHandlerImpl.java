package ru.yandex.yamblz.concurrency.sync.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

/**
 * Just for lulz
 */
public class UncaughtExceptionHandlerImpl extends Synchronizer {

    public UncaughtExceptionHandlerImpl(SyncParameters params) {
        super(params);
    }


    protected void customSync() {
        GoofyHandler handler = new GoofyHandler(new PostConsumer(params.postFinish));

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            StrangeProducer producer = new StrangeProducer(params.dataResults, params.postResult);
            producer.setUncaughtExceptionHandler(handler);
            producer.start();
        }
    }


    private static class GoofyHandler implements UncaughtExceptionHandler {
        final AtomicInteger counter = new AtomicInteger();
        final PostConsumer callback;

        public GoofyHandler(PostConsumer callback) {
            this.callback = callback;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if (counter.incrementAndGet() == PRODUCERS_COUNT) {
                callback.start();
            }
        }
    }


    private static final class StrangeProducer extends LoadProducer {
        public StrangeProducer(Set<String> resultSet, Runnable onResult) {
            super(resultSet, onResult);
        }

        @Override
        public void synchronize() {
            throw new Error("Wow!");
        }
    }
}
