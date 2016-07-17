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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {


    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    public static final int PRODUCERS_COUNT = 5;

    private long startTime, endTime;

    @BindView(R.id.hello)
    TextView helloView;

    @NonNull
    private final Set<String> dataResults = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static Semaphore SEMAPHORE = new Semaphore(1, true);


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.nanoTime();

        // Semaphore не слишком подходит для решения данной задачи, и другие разработчики могут быть
        // немного удивлены вашим выбором, хотя и не слишком сильно.
        // Для решения данной задачи через семафор можно ограничить доступ к ресурсу и позваолить
        // только одному потоку одновременно выполняться. Но в этом случае решение будет последовательным
        // и очень медленным. Не надо так.
        // Кроме того, нет гарантии, что потоки будут выполнены именно в той последовательности,
        // в которой они были созданы, а значит, нужно вводить дополнительную логику.

        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            new LoadProducer(dataResults, this::postResult, new PostConsumer(this::postFinish)).start();
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
