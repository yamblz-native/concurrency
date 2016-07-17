package ru.yandex.yamblz.concurrency;

import java.util.Set;
import java.util.concurrent.Exchanger;

import ru.yandex.yamblz.ui.fragments.ContentFragment;

/**
 * Created by user on 17.07.16.
 */
public class ProducersManager extends Thread {

    private final int count;
    private final Set<String> dataResults;
    private final Runnable postResult;
    private final Exchanger<String> producersExchanger = new Exchanger<>();

    public ProducersManager(int count, Set<String> dataResults, Runnable postResult) {

        this.count = count;
        this.dataResults = dataResults;
        this.postResult = postResult;
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < count; i++) {
            new LoadProducer(dataResults, postResult, producersExchanger).start();
        }

        try {
            while (true) {
                ContentFragment.EXCHANGER.exchange(dataResults);
                if (dataResults.size() == ContentFragment.PRODUCERS_COUNT) break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
