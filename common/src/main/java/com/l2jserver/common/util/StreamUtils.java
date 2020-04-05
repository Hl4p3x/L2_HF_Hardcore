package com.l2jserver.common.util;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T> Stream<T> streamOptional(Optional<T> optional) {
        return optional.map(Stream::of).orElse(Stream.empty());
    }

}
