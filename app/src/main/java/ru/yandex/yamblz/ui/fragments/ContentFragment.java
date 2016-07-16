package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.sync.SyncBuilder;
import ru.yandex.yamblz.concurrency.sync.SyncBuilder.Type;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    public static final int PRODUCERS_COUNT = 5;

    private final SyncBuilder syncBuilder = new SyncBuilder();

    @BindView(R.id.hello) TextView helloView;

    @NonNull private final Set<String> dataResults = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }


    final void postResult() {
        runOnUiThreadIfFragmentAlive(() -> helloView.setText(String.valueOf(dataResults.size())));
    }


    final void postFinish() {
        if (dataResults.size() < PRODUCERS_COUNT) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        runOnUiThreadIfFragmentAlive(() -> helloView.setText(R.string.task_win));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        syncBuilder.init(dataResults, this::postResult, this::postFinish, (ViewGroup) getView());
    }


    @OnClick(R.id.semaphore)
    public void semaphoreImpl() {
        syncBuilder.build(Type.SEMAPHORE).sync();
    }


    @OnClick(R.id.count_down_latch)
    public void countDownLatchImpl() {
        syncBuilder.build(Type.COUNT_DOWN_LATCH).sync();
    }


    @OnClick(R.id.cyclic_barrier)
    public void cyclicBarrierImpl() {
        syncBuilder.build(Type.CYCLIC_BARRIER).sync();
    }
}
