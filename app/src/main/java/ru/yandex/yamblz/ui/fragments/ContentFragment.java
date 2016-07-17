package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {


    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;

    private long startTime, endTime;

    @BindView(R.id.hello)
    TextView helloView;

    @NonNull
    private final Set<String> dataResults = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static CyclicBarrier CYCLIC_BARRIER;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.nanoTime();

        // CyclicBarrier предназначен для того, чтобы подождать, пока несколько потоков соберутся
        // в некоторой "точке сбора" и затем, если указано, выполнить некий runnable. В данном случае
        // в роли runnable-действия можно взять старт PostConsumer'a.

        CYCLIC_BARRIER = new CyclicBarrier(PRODUCERS_COUNT, () -> new PostConsumer(this::postFinish).start());
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new LoadProducer(dataResults, this::postResult).start();
        }

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

        endTime = System.nanoTime();
        Log.d("Execution time", String.valueOf(TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS) + "ms"));

        runOnUiThreadIfFragmentAlive(() -> {
            assert helloView != null;
            helloView.setText(R.string.task_win);

        });
    }
}
