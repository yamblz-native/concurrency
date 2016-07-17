package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class CyclicBarrierImpl extends Synchronizer {

    public CyclicBarrierImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(PRODUCERS_COUNT, new PostConsumer(params.postFinish));

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, cyclicBarrier).start();
        }
    }


    private static final class Producer extends LoadProducer {
        final CyclicBarrier cyclicBarrier;

        public Producer(Set<String> resultSet, Runnable onResult, CyclicBarrier cyclicBarrier) {
            super(resultSet, onResult);
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void synchronize() throws BrokenBarrierException, InterruptedException {
            cyclicBarrier.await();
        }
    }
}
