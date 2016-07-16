package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.Set;

/**
 * Simple result consumer thread; non-extensible
 *
 * @author archinamon on 13/07/16.
 */

public final class PostConsumer extends Thread {

    @NonNull
    private final Runnable onFinish;
    private final WaitNotifyLock lock;

    public PostConsumer(@NonNull Runnable onFinish, WaitNotifyLock lock) {
        this.onFinish = onFinish;
        this.lock = lock;
    }

    @Override
    public void run() {
        super.run();

        synchronized (lock) {
            // Почему поле должно быть финальным? - Если монитор будет изменен, то
            // лок будет стоять на разных инстансах объекта, и синхронизация будет работать для
            // разных инстансов объекта. Таким образом, лока на самом деле происходить не будет
            // и произойдет race condition.

            // С какого момента произойдет восстановление процесса работы потока? - С момента вызова .wait()

            // Что произойдет, если не вызвать метод notify()? - Тред будет разбужен и попробует получить
            // доступ к ресурсу в недетерминированное время, в зависимости от желания левой пятки jvm
            Log.d("CONSUMER", "Consumer enters sync block");
            while (lock.getThreads() > 0) {
                try {
                    Log.d("CONSUMER", "Consumer waits");
                    lock.wait(1); // Как выбрать правильное время ожидания для треда? -
                    Log.d("CONSUMER", "Consumer stopped waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            Log.d("CONSUMER", "Consumer finished execution");
            onFinish.run();

        }

    }


}
