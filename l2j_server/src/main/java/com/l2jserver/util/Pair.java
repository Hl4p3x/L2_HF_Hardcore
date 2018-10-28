package com.l2jserver.util;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Pair<T, Y> {

    private T left;
    private Y right;

    public Pair(T left, Y right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public Y getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Pair.class.getSimpleName() + "[", "]")
                .add(Objects.toString(left))
                .add(Objects.toString(right))
                .toString();
    }

    public static <T> Pair<T, T> of(List<T> items) {
        if (items.size() == 2) {
            return new Pair<>(items.get(0), items.get(1));
        } else {
            throw new IllegalArgumentException("Cannot create a pair of non paired size element list: " + items);
        }
    }

}
