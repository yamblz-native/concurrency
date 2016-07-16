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
import java.util.concurrent.Phaser;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.concurrency.LoadProducer;
import ru.yandex.yamblz.concurrency.PostConsumer;

@SuppressWarnings("WeakerAccess")
public class ContentFragment extends BaseFragment {

    private static final String CONSUME_EXCEPTION = "Some producers not finished yet!";
    private static final int PRODUCERS_COUNT = 5;

    @BindView(R.id.hello) TextView helloView;

    //В данном примере использование потокобезопасных коллекций кажется избыточным, ибо только добавляем
    //При необходимости изменять содержимое коллекции во время итерации по элементам надо бы юзать что-нибудь потокобезопасное
    @NonNull private final Set<String> dataResults = new LinkedHashSet<>();

    //Использование Phaser в Android приложенини кажется не очень обоснованным в виду его новизны,
    //но ограничений по поддерживаемым версиям в задании нет, и больно хочется
    @NonNull private final Phaser phaser = new Phaser(1);// 1 for PostConsumer
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        new PostConsumer(this::postFinish).start();
        for (int i = 0; i < PRODUCERS_COUNT; i++) {
            // Регестрируем новый поток в фазере и активирем его
            phaser.register();
            new LoadProducer(dataResults, this::postResult).start();
        }
    }

    final void postResult() {
        runOnUiThreadIfFragmentAlive(()->{
            helloView.setText(String.valueOf(dataResults.size()));
        });
        phaser.arriveAndDeregister();
    }

    final void postFinish() {
        //PostConsumer покорно ждет, пока остальные закончат
        phaser.arriveAndAwaitAdvance();
        //После освобождения проверяем на случай любых махинаций с фрагментов
        //Не выполняем логику, если в этом нет необходимости
        if(!phaser.isTerminated()){
            if (dataResults.size() < PRODUCERS_COUNT) {
                throw new RuntimeException(CONSUME_EXCEPTION);
            }
            runOnUiThreadIfFragmentAlive(()-> {
                helloView.setText(R.string.task_win);
            });
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //Дабы не допустить утечку памяти в виде ожидающего чуда PostConsumer,
        //освобождаем все схвашеные фазером потоки
        phaser.forceTermination();
    }
}
