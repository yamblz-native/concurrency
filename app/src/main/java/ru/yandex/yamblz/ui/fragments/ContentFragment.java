package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;
    private static final String LOG_TAG = "Yamblz:ContentFragment";
    private PostConsumer postConsumer = null;

    @BindView(R.id.hello) TextView helloView;

    @NonNull private final Set<String> dataResults = new LinkedHashSet<>();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        List<LoadProducer> producerList = new ArrayList<>(PRODUCERS_COUNT);
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            LoadProducer producer = new LoadProducer(dataResults, this::postResult);
            producerList.add(producer);
            producer.start();
        }
        postConsumer = new PostConsumer(this::postFinish, producerList);
        postConsumer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
        postConsumer.interrupt();
    }

    final void postResult() {
//        assert helloView != null;
        runOnUiThreadIfFragmentAlive(new Runnable() {
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

//        assert helloView != null;
        runOnUiThreadIfFragmentAlive(new Runnable() {
            @Override
            public void run() {
                helloView.setText(R.string.task_win);
            }
        });

    }
}
