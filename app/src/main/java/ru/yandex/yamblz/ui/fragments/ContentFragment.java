package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.ui.activities.MainActivity;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;
    CyclicBarrier barrier = new CyclicBarrier(ContentFragment.PRODUCERS_COUNT + 1);

    @BindView(R.id.hello) TextView helloView;

    @NonNull private final Set<String> dataResults = new LinkedHashSet<>();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        new PostConsumer(this::postFinish, barrier).start();
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new LoadProducer(dataResults, this::postResult, barrier).start();
        }

    }

    final void postResult() {
        assert helloView != null;
        helloView.post(new Runnable() {
            @Override
            public void run() {
                helloView.setText(String.valueOf(dataResults.size()));
            }
        });

    }

    final void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        assert helloView != null;

        helloView.post(new Runnable() {
            @Override
            public void run() {
                helloView.setText(R.string.task_win);
            }
        });

    }
}
