package com.l2jserver.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HeadTail<T> {

    private final Optional<T> head;
    private final List<T> tail;

    public HeadTail() {
        this.head = Optional.empty();
        this.tail = Collections.emptyList();
    }

    public HeadTail(T head, List<T> tail) {
        this.head = Optional.of(head);
        this.tail = tail;
    }

    public Optional<T> getHead() {
        return head;
    }

    public List<T> getTail() {
        return tail;
    }

    public static <T> HeadTail<T> empty() {
        return new HeadTail<T>();
    }

    @Override
    public String toString() {
        return "HeadTail[" +
                "head=" + head +
                ", tail=" + tail +
                ']';
    }
}