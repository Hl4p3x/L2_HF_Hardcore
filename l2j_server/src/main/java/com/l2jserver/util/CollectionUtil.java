package com.l2jserver.util;

import com.google.common.collect.Lists;
import com.l2jserver.gameserver.model.interfaces.IIdentifiable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtil {

    public static <T> List<List<T>> splitList(List<T> items, int parts) {
        if (items.size() < parts) {
            return Collections.singletonList(items);
        }

        int partSize = items.size() / parts;
        List<Integer> partSizes = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            partSizes.add(partSize);
        }

        int leftover = items.size() % parts;
        int leftoverIndex = 0;
        while (leftover > 0) {
            if (leftoverIndex >= items.size()) {
                leftoverIndex = 0;
            }
            partSizes.set(leftoverIndex, partSizes.get(leftoverIndex) + 1);
            leftover -= 1;
            leftoverIndex += 1;
        }

        int partPosition = 0;
        List<List<T>> results = new ArrayList<>();
        for (Integer partSizeItem : partSizes) {
            results.add(items.subList(partPosition, partPosition + partSizeItem));
            partPosition = partPosition + partSizeItem;
        }
        return results;

    }

    public static <T extends IIdentifiable> Set<Integer> extractIds(Collection<T> items) {
        return extract(items, IIdentifiable::getId);
    }

    public static <T, R> Set<R> extract(Collection<T> items, Function<T, R> extractor) {
        return items.stream().map(extractor::apply).collect(Collectors.toSet());
    }

    public static <T> void addIfPresent(Collection<T> items, Optional<T> item) {
        item.ifPresent(items::add);
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
