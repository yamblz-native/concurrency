package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.concurrency.ProducersThread;
import ru.yandex.yamblz.concurrency.WaitNotifyLock;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {


    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;

    @BindView(R.id.hello)
    TextView helloView;

    @NonNull
    private final Set<String> dataResults = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        // TODO: Сделать логирование, чекнуть мемори и время выполнения
        super.onResume();
        Log.d(Calendar.getInstance().getTime().toString(), "Fragment starts threading");
        ProducersThread producersThread = new ProducersThread(PRODUCERS_COUNT, dataResults, this::postResult);
        producersThread.start();
        PostConsumer consumer = new PostConsumer(producersThread, this::postFinish);
        consumer.start();

    }

    final void postResult() {
        runOnUiThreadIfFragmentAlive(() -> {
            assert helloView != null;
            helloView.setText(String.valueOf(dataResults.size()));
        });
    }

    final void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        runOnUiThreadIfFragmentAlive(() -> {
            assert helloView != null;
            helloView.setText(R.string.task_win);

        });
    }
}
