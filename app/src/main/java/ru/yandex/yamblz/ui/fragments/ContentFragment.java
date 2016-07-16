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
import java.util.concurrent.TimeUnit;

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

    private long startTime, endTime;

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
        super.onResume();
        startTime = System.nanoTime();

        // Теперь попробуем то же самое, только с join-ом
        // join блокирует выполнение текущего треда до тех пор, пока не завершится другой
        // Значит, нам нужен еще один тред, который будет следить за выполнением Producer-тредов
        // и завершится только после выполнения их всех.
        // Можно сделать это только join-ами, но в таком случае, Producer тредам придется выполняться
        // последовательно, что совершенно не круто. Либо используем wait / notify и наш уже написанный
        // WaitNotifyLock
        // В любом случае, много лишего кода, понимание затрудняется и вообще фу.
        // Резюме: не стоит использовать join для синхронизации нескольких потоков (по крайней мере,
        // если они создаются не из друг друга
        if (dataResults.size() < PRODUCERS_COUNT) {
            WaitNotifyLock locker = new WaitNotifyLock(PRODUCERS_COUNT);
            ProducersThread joinThread = new ProducersThread(locker, dataResults, this::postResult);
            joinThread.start();
            new PostConsumer(joinThread, this::postFinish).start();

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
