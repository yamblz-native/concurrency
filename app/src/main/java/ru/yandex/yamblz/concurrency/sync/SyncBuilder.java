package ru.yandex.yamblz.concurrency.sync;

import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.Set;

import ru.yandex.yamblz.concurrency.sync.impl.BlockingQueueImpl;
import ru.yandex.yamblz.concurrency.sync.impl.CountDownLatchImpl;
import ru.yandex.yamblz.concurrency.sync.impl.CyclicBarrierImpl;
import ru.yandex.yamblz.concurrency.sync.impl.ExchangerImpl;
import ru.yandex.yamblz.concurrency.sync.impl.FutureImpl;
import ru.yandex.yamblz.concurrency.sync.impl.JoinImpl;
import ru.yandex.yamblz.concurrency.sync.impl.LockImpl;
import ru.yandex.yamblz.concurrency.sync.impl.PhaserImpl;
import ru.yandex.yamblz.concurrency.sync.impl.SemaphoreImpl;
import ru.yandex.yamblz.concurrency.sync.impl.SleepInterruptImpl;
import ru.yandex.yamblz.concurrency.sync.impl.UncaughtExceptionHandlerImpl;
import ru.yandex.yamblz.concurrency.sync.impl.WaitNotifyImpl;

/**
 * From the architectural standpoint it is better to refactor this
 * into some kind of abstract factory or something even simpler,
 * but I tried to preserve the given structure as much as possible
 * (e.g. callbacks and shared result set), so we have what we have.
 */

public final class SyncBuilder {

    private SyncParameters params;

    public void init(Set<String> dataResults, Runnable postResult, Runnable postFinish, ViewGroup rootView) {
        params = new SyncParameters(dataResults, postResult, postFinish, rootView);
    }

    public Synchronizer build(Type type) {
        switch (type) {
            case SEMAPHORE:
                return new SemaphoreImpl(params);
            case COUNT_DOWN_LATCH:
                return new CountDownLatchImpl(params);
            case CYCLIC_BARRIER:
                return new CyclicBarrierImpl(params);
            case PHASER:
                return new PhaserImpl(params);
            case EXCHANGER:
                return new ExchangerImpl(params);
            case BLOCKING_QUEUE:
                return new BlockingQueueImpl(params);
            case FUTURE:
                return new FutureImpl(params);
            case LOCK:
                return new LockImpl(params);
            case JOIN:
                return new JoinImpl(params);
            case WAIT_NOTIFY:
                return new WaitNotifyImpl(params);
            case SLEEP_INTERRUPT:
                return new SleepInterruptImpl(params);
            case UNCAUGHT_EXCEPTION_HANDLER:
                return new UncaughtExceptionHandlerImpl(params);
            default:
                throw new InvalidParameterException();
        }
    }

    public enum Type {
        SEMAPHORE,
        COUNT_DOWN_LATCH,
        CYCLIC_BARRIER,
        PHASER,
        EXCHANGER,
        BLOCKING_QUEUE,
        FUTURE,
        LOCK,
        JOIN,
        WAIT_NOTIFY,
        SLEEP_INTERRUPT,
        UNCAUGHT_EXCEPTION_HANDLER,
    }
}
