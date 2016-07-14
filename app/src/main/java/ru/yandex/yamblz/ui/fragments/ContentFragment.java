package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        PostConsumer postConsumer = new PostConsumer(this::postFinish, PRODUCERS_COUNT);
        CountDownLatch countDownLatch = postConsumer.getCountDownLatch();
        postConsumer.start();
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new LoadProducer(dataResults, this::postResult, countDownLatch)
                    .start();
        }
    }

    final synchronized void postResult() {
        assert helloView != null;
        runOnUiThreadIfFragmentAlive(() -> helloView.setText(String.valueOf(dataResults.size())));
    }

    final synchronized void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        assert helloView != null;
        runOnUiThreadIfFragmentAlive(() -> helloView.setText(R.string.task_win));
    }
}
