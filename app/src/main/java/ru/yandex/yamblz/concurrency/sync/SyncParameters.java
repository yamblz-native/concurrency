package ru.yandex.yamblz.concurrency.sync;

import android.view.ViewGroup;

import java.util.Set;

public final class SyncParameters {
    public final Runnable postResult;
    public final Runnable postFinish;
    public final Set<String> dataResults;
    public final ViewGroup rootView;

    SyncParameters(Set<String> dataResults, Runnable postResult, Runnable postFinish, ViewGroup rootView) {
        this.dataResults = dataResults;
        this.postResult = postResult;
        this.postFinish = postFinish;
        this.rootView = rootView;
    }
}
