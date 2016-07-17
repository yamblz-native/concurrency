package ru.yandex.yamblz.concurrency.sync;

import android.view.View;
import android.widget.Button;

public abstract class Synchronizer {

    protected final SyncParameters params;

    public Synchronizer(SyncParameters params) {
        this.params = params;
    }

    public void sync() {
        disableButtons();
        customSync();
    }

    private void disableButtons() {
        for (int i = 0; i < params.rootView.getChildCount(); i++) {
            View view = params.rootView.getChildAt(i);
            if (view instanceof Button) {
                view.setEnabled(false);
            }
        }
    }

    protected abstract void customSync();
}
