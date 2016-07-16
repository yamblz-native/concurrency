package ru.yandex.yamblz.concurrency.sync.impl;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

public class BlockingQueueImpl extends Synchronizer {

    public BlockingQueueImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {

        // Payload is transferred via shared set, so just send something
        BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(PRODUCERS_COUNT);

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, blockingQueue).start();
        }

        new Consumer(params.postFinish, blockingQueue).start();
    }


    private static final class Producer extends LoadProducer {
        private BlockingQueue<Object> blockingQueue;

        public Producer(Set<String> resultSet, Runnable onResult, BlockingQueue<Object> blockingQueue) {
            super(resultSet, onResult);
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void synchronize() {
            blockingQueue.add(Object.class);
        }
    }


    private static final class Consumer extends PostConsumer {
        private BlockingQueue<Object> blockingQueue;

        public Consumer(Runnable onFinish, BlockingQueue<Object> blockingQueue) {
            super(onFinish);
            this.blockingQueue = blockingQueue;
        }

        @Override
        protected void synchronize() throws InterruptedException {
            for (int i = 0; i < PRODUCERS_COUNT; i++) {
                blockingQueue.take();
            }
        }
    }
}
