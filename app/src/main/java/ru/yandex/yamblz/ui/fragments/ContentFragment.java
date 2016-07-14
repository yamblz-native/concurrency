package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;
    private final CyclicBarrier barrier = new CyclicBarrier(PRODUCERS_COUNT+1);
    private final List<Thread> threads = new ArrayList<>();

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
        PostConsumer postConsumer=new PostConsumer(this::postFinish,barrier);
        threads.add(postConsumer);
        postConsumer.start();
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            LoadProducer loadProducer=new LoadProducer(dataResults,barrier, this::postResult);
            threads.add(loadProducer);
            loadProducer.start();
        }
    }

    final void postResult() {
        assert helloView != null;
        runOnUiThreadIfFragmentAlive(()->helloView.setText(String.valueOf(dataResults.size())));
    }

    final void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        assert helloView != null;
        runOnUiThreadIfFragmentAlive(()->helloView.setText(R.string.task_win));
    }

    @Override
    public void onPause() {
        super.onPause();
        for(Thread thread:threads){
            thread.interrupt();
        }
    }
}
