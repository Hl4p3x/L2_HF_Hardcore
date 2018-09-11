package com.l2jserver.util;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CollectionUtil {

    public static <T> List<List<T>> splitList(List<T> items, int parts) {
        if (items.size() < parts) {
            return Collections.singletonList(items);
        }

        int partSize = (int) Math.ceil((double) items.size() / parts);
        List<List<T>> results = new ArrayList<>();

        int partStart = 0;
        int partEnd = partSize;
        for (int i = 0; i < parts; i++) {
            results.add(items.subList(partStart, partEnd));
            partStart = partEnd;
            partEnd += partSize;
            if (partEnd > items.size()) {
                partEnd = items.size();
            }
        }
        return results;
    }

    public static <T> HeadTail<T> beheaded(Collection<T> collection) {
        if (collection.isEmpty()) {
            return HeadTail.empty();
        }

        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return HeadTail.empty();
        }

        return new HeadTail<>(iterator.next(), Lists.newArrayList(iterator));
    }

    public static <T, R> List<R> consecutiveMap(Collection<T> collection, BiFunction<T, T, R> function) {
        List<R> results = new ArrayList<>();
        consecutiveForEach(collection, (T left, T right) -> {
            results.add(function.apply(left, right));
        });
        return results;
    }

    public static <T> void consecutiveForEach(Collection<T> collection, BiConsumer<T, T> consecutiveConsumer) {
        Iterator<T> iterator = collection.iterator();
        T current;
        T next;

        if (!iterator.hasNext()) {
            return;
        }
        current = iterator.next();

        while (iterator.hasNext()) {
            next = iterator.next();
            consecutiveConsumer.accept(current, next);
            current = next;
        }
    }

}
