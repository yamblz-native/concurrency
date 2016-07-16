package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    @BindView(R.id.btn_container) LinearLayout btnContainer;

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
        syncBuilder.init(dataResults, this::postResult, this::postFinish, btnContainer);
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


    @OnClick(R.id.phaser)
    public void phaserImpl() {
        syncBuilder.build(Type.PHASER).sync();
    }


    @OnClick(R.id.exchanger)
    public void exchangerImpl() {
        syncBuilder.build(Type.EXCHANGER).sync();
    }


    @OnClick(R.id.blocking_queue)
    public void blockingQueueImpl() {
        syncBuilder.build(Type.BLOCKING_QUEUE).sync();
    }


    @OnClick(R.id.future)
    public void futureImpl() {
        syncBuilder.build(Type.FUTURE).sync();
    }


    @OnClick(R.id.lock)
    public void lockImpl() {
        syncBuilder.build(Type.LOCK).sync();
    }


    @OnClick(R.id.join)
    public void joinImpl() {
        syncBuilder.build(Type.JOIN).sync();
    }


    @OnClick(R.id.wait_notify)
    public void waitNotifyImpl() {
        syncBuilder.build(Type.WAIT_NOTIFY).sync();
    }


    @OnClick(R.id.uncaught_exception_handler)
    public void uncaughtExceptionHandlerImpl() {
        syncBuilder.build(Type.UNCAUGHT_EXCEPTION_HANDLER).sync();
    }
}
