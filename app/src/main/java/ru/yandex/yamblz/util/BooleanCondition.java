package ru.yandex.yamblz.util;

import java.io.Serializable;

/**
 * Created by root on 7/15/16.
 */
public class BooleanCondition implements Serializable {

    private boolean value;

    public BooleanCondition() {
        this(false);
    }

    public BooleanCondition(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
