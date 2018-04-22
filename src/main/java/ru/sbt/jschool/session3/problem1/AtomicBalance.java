package ru.sbt.jschool.session3.problem1;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicBalance {
    private final AtomicReference<Float> value = new AtomicReference<>();

    public AtomicBalance(Float value) {
        this.value.set(value);
    }

    public Float get() {
        return value.get();
    }

    public boolean increment(Float v) {
        Float current;
        Float next;
        do {
            current = value.get();
            next = current + v;
        } while (!value.compareAndSet(current, next));
        return true;
    }

    public boolean decrement(Float v) {
        Float current;
        Float next;
        do {
            current = value.get();
            next = current - v;
            if(next < 0) {
                return false;
            }
        } while (!value.compareAndSet(current, next));
        return true;
    }
}
