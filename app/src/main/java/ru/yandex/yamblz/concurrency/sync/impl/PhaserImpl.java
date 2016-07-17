package ru.yandex.yamblz.concurrency.sync.impl;

import android.annotation.TargetApi;
import android.widget.Toast;

import java.util.Set;
import java.util.concurrent.Phaser;

import ru.yandex.yamblz.App;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.sync.SyncParameters;
import ru.yandex.yamblz.concurrency.sync.Synchronizer;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static ru.yandex.yamblz.ui.fragments.ContentFragment.PRODUCERS_COUNT;

@TargetApi(LOLLIPOP)
public class PhaserImpl extends Synchronizer {

    public PhaserImpl(SyncParameters params) {
        super(params);
    }


    @Override
    protected void customSync() {

        if (SDK_INT < LOLLIPOP) {
            Toast.makeText(App.get(params.rootView.getContext()), R.string.not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        Phaser phaser = new Phaser(PRODUCERS_COUNT);

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new Producer(params.dataResults, params.postResult, phaser).start();
        }

        new Consumer(params.postFinish, phaser).start();
    }


    private static final class Producer extends LoadProducer {
        final Phaser phaser;

        public Producer(Set<String> resultSet, Runnable onResult, Phaser phaser) {
            super(resultSet, onResult);
            this.phaser = phaser;
        }

        @Override
        public void synchronize() {
            phaser.arriveAndDeregister();
        }
    }


    private static final class Consumer extends PostConsumer {
        private final Phaser phaser;

        public Consumer(Runnable onFinish, Phaser phaser) {
            super(onFinish);
            this.phaser = phaser;
        }

        @Override
        protected void synchronize() {
            phaser.awaitAdvance(phaser.getPhase());
        }
    }
}
