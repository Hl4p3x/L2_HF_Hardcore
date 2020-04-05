package com.l2jserver.common.util;

import java.util.Objects;

public class Holder<T> {

    private T item;

    public Holder(T item) {
        this.item = item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holder<?> holder = (Holder<?>) o;
        return Objects.equals(item, holder.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "Holder{" +
                "item=" + item +
                '}';
    }

}
