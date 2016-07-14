package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashSet;
import java.util.Set;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    public static final int PRODUCERS_COUNT = 5;

    @BindView(R.id.hello)
    TextView helloView;

    @NonNull
    private final Set<String> dataResults = new LinkedHashSet<>();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadProducer.loadedProducersSetToZero();
        dataResults.clear();

        new PostConsumer(this::postFinish).start();
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new LoadProducer(dataResults, this::postResult).start();
        }
    }

    @MainThread
    final void postResult() {
        assert helloView != null;
        helloView.setText(String.valueOf(dataResults.size()));
    }

    @MainThread
    final void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        assert helloView != null;
        helloView.setText(R.string.task_win);
    }
}
