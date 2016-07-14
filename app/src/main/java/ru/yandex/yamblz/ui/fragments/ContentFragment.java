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
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;
    private boolean producersIsExist;

    @BindView(R.id.hello) TextView helloView;

    @NonNull private final Set<String> dataResults = new LinkedHashSet<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle)
    {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!producersIsExist)
        {
            dataResults.clear();
            CountDownLatch countDownLatch = new CountDownLatch(PRODUCERS_COUNT);

            new PostConsumer(this::postFinish, countDownLatch).start();

            for (int i = 0; i < PRODUCERS_COUNT; i++)
            {
                new LoadProducer(dataResults, this::postResult, countDownLatch).start();
            }

            producersIsExist = true;
        }
    }

    final void postResult()
    {
        runOnUiThreadIfFragmentAlive(() -> helloView.setText(String.valueOf(dataResults.size())));
    }

    final void postFinish()
    {
        runOnUiThreadIfFragmentAlive(() ->
        {
            if (dataResults.size() < PRODUCERS_COUNT) {
                throw new RuntimeException(CONSUME_EXCEPTION);
            }
            helloView.setText(R.string.task_win);
        });

        producersIsExist = false;
    }
}
