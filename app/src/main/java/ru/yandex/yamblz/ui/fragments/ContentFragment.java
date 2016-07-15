package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CyclicBarrier;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;
import ru.yandex.yamblz.util.BooleanCondition;

import static ru.yandex.yamblz.util.AndroidUtils.setText;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final String LOG_TAG = "synchronization";
    private static final String DATA_RESULTS_KEY = "ru.yandex.yamblz.ui.fragments.ContentFragment.dataResults";
    private static final String IS_DONE_KEY = "ru.yandex.yamblz.ui.fragments.ContentFragment.isDone";
    private static final int PRODUCERS_COUNT = 5;

    @BindView(R.id.hello) TextView helloView;

    /**
     * Changed dataResults to CopyOnWrite ArraySet to avoid 'final'qualifier. It's necessary
     * to save and recovery data from Bundle.
     */
    @NonNull private CopyOnWriteArraySet<String> dataResults;

    @NonNull private CyclicBarrier cyclicBarrier;

    private BooleanCondition isDone = new BooleanCondition(false);

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            if (savedInstanceState.getSerializable(DATA_RESULTS_KEY) != null) {
                dataResults = (CopyOnWriteArraySet<String>) savedInstanceState.getSerializable(DATA_RESULTS_KEY);
            }

            if(savedInstanceState.getSerializable(IS_DONE_KEY) != null) {
                isDone = (BooleanCondition) savedInstanceState.getSerializable(IS_DONE_KEY);
            }
        }

        return inflater.inflate(R.layout.fragment_content, container, false);
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

        outState.putSerializable(DATA_RESULTS_KEY, dataResults);
        outState.putSerializable(IS_DONE_KEY, isDone);
    }

    private void setFinishText() {

        if(helloView != null) {
            setText(getActivity(), helloView, R.string.task_win);
        }
    }

    private void setResultText() {

        if(helloView != null) {
            setText(getActivity(), helloView, String.valueOf(dataResults.size()));
        }
    }

}
