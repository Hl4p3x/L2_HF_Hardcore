package com.l2jserver.common.util;

import java.util.List;

public class RndCollection {

    public static <T> T random(List<T> collection) {
        return collection.get(Rnd.get(collection.size()));
    }

}
