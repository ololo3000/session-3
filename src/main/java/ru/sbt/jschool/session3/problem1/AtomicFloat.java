package ru.sbt.jschool.session3.problem1;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicFloat {
    private final AtomicReference<Float> value = new AtomicReference<>();

    public AtomicFloat(Float value) {
        this.value.set(value);
    }

    public Float get() {
        return value.get();
    }

    public void set(Float v) {
        while (true) {
            Float current = value.get();
            if (value.compareAndSet(current, v)) {
                return;
            }
        }
    }
}
