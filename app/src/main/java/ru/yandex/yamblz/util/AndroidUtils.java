package ru.yandex.yamblz.util;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by root on 7/15/16.
 */
public class AndroidUtils {

    public static void setText(Activity activity, TextView textView, String str) {
        activity.runOnUiThread(() -> textView.setText(str));
    }

    public static void setText(Activity activity, TextView textView, int resId) {
        activity.runOnUiThread(() -> textView.setText(resId));
    }

}
