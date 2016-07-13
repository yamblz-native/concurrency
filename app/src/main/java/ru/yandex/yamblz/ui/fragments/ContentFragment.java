package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";

    @BindView(R.id.hello) TextView helloView;

    private final int producersCount = 5;

    @NonNull private final Set<String> dataResults = new LinkedHashSet<>();
    @NonNull private final List<LoadProducer> tasks = new LinkedList<>();

    /*dynamic*/ {
        for (int i = 0; i < producersCount; i++) {
            tasks.add(new LoadProducer(dataResults, this::postResult));
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        new PostConsumer(this::postFinish).start();
        for (LoadProducer producer : tasks) {
            producer.start();
        }
    }

    final void postResult() {
        helloView.setText(dataResults.size());
    }

    final void postFinish() {
        if (dataResults.size() < producersCount) {
            throw new RuntimeException(CONSUME_EXCEPTION);
        }

        helloView.setText(R.string.task_win);
    }
}
