package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @NonNull private final Set<String> dataResults = Collections.synchronizedSet(new LinkedHashSet<>());

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_content, container, false);
        if (savedInstanceState != null && savedInstanceState.containsKey("text")) {
            TextView view = (TextView)inflate.findViewById(R.id.hello);
            view.setText(savedInstanceState.getCharSequence("text"));
        }
        return inflate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("text", helloView.getText());
    }


    private boolean isLoading = false;
    @Override
    public void onResume() {
        super.onResume();
        if (!isLoading) startLoading();
    }

    private void startLoading() {
        CountDownLatch countDownLatch = new CountDownLatch(PRODUCERS_COUNT);
        new PostConsumer(this::postFinish, countDownLatch).start();
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new LoadProducer(dataResults, this::postResult, countDownLatch).start();
        }
        isLoading = true;
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
        helloView.invalidate();
    }


}
