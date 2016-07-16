package ru.yandex.yamblz.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;

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

        synchronized (lock) { // Почему поле должно быть финальным?
            // С какого момента произойдет восстановление процесса работы потока? - С момента вызова .wail()
            // Что произойдет, если не вызвать метод notify()?
            Log.d(Calendar.getInstance().getTime().toString(), "Consumer enters sync block");
            while (lock.getThreads() > 0) {
                try {
                    Log.d(Calendar.getInstance().getTime().toString(), "Consumer waits for 1 sec");
                    lock.wait(1000);
                    Log.d(Calendar.getInstance().getTime().toString(), "Consumer stopped waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            onFinish.run();

        }

    }


}
