package ru.yandex.yamblz.ui.fragments;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CyclicBarrier;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.util.BooleanCondition;

import static ru.yandex.yamblz.util.AndroidUtils.setText;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final String LOG_TAG = "synchronization";
    private static final int PRODUCERS_COUNT = 5;

    @BindView(R.id.hello) TextView helloView;

    @NonNull private CyclicBarrier cyclicBarrier;

    @State @NonNull CopyOnWriteArraySet<String> dataResults;
    @State BooleanCondition isDone = new BooleanCondition(false);

    @Nullable private static WeakReference<ContentFragment> actualInstance;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        Icepick.restoreInstanceState(this, savedInstanceState);
        actualInstance = new WeakReference<>(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(dataResults != null) {

            if(isDone.getValue()) {
                setFinishText();
            } else {
                setResultText();
            }
            return;
        }

        cyclicBarrier = new CyclicBarrier(PRODUCERS_COUNT, new PostConsumer(this::postFinish));
        dataResults = new CopyOnWriteArraySet<>();
        for (int i = 0; i < PRODUCERS_COUNT; ++i) {
            new LoadProducer(dataResults, this::postResult).start();
        }
    }

    private final void postResult() {

        Log.d(LOG_TAG, "Working " + dataResults.size() + " producer");

        setResultText();

        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
            return;
        }

    }

    private final void postFinish() {

        Log.d(LOG_TAG, "Working consumer");

        isDone.setValue(true);

        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        setFinishText();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void setFinishText() {
        if(actualInstance.get() != null) {
            setText(actualInstance.get().getActivity(), actualInstance.get().helloView, R.string.task_win);
        }
    }

    private void setResultText() {
        if(actualInstance.get() != null) {
            setText(actualInstance.get().getActivity(), actualInstance.get().helloView, String.valueOf(dataResults.size()));
        }
    }
}
