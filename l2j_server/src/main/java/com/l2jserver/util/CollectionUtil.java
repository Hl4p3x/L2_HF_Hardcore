package com.l2jserver.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CollectionUtil {

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
