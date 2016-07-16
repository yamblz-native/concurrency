package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;

    @BindView(R.id.hello) TextView helloView;

    @NonNull private final Set<String> dataResults = new ConcurrentSkipListSet<>();
    PostConsumer consumerThread;
    @NonNull private final List<Thread> threads = new ArrayList<>();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Creating consumer thread */
        consumerThread = new PostConsumer(this::postFinish, PRODUCERS_COUNT, this);
        threads.add(consumerThread);
        consumerThread.start();

        /* Creating producers threads */
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            LoadProducer loadProducer = new LoadProducer(dataResults, this::postResult,
                    consumerThread.getCyclicBarrier());
            threads.add(loadProducer);
            loadProducer.start();
        }
    }

    final void postResult() {
        assert helloView != null;
        helloView.setText(String.valueOf(dataResults.size()));
    }

    final void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        assert helloView != null;
        helloView.setText(R.string.task_win);
    }

    @Override
    // Not sure if that's ok
    public void runOnUiThreadIfFragmentAlive(@NonNull Runnable runnable) {
        super.runOnUiThreadIfFragmentAlive(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
