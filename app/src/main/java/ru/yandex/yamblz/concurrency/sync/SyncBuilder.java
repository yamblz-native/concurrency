package ru.yandex.yamblz.concurrency.sync;

import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.Set;

import ru.yandex.yamblz.concurrency.sync.impl.CountDownLatchImpl;
import ru.yandex.yamblz.concurrency.sync.impl.SemaphoreImpl;

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
            case COUNT_DOWN_LATCH:
                return new CountDownLatchImpl(params);
            case SEMAPHORE:
                return new SemaphoreImpl(params);
            default:
                throw new InvalidParameterException();
        }
    }

    public enum Type {
        COUNT_DOWN_LATCH,
        SEMAPHORE
    }
}
