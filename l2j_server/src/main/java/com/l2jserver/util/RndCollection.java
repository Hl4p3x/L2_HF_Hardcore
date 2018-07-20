package com.l2jserver.util;

import java.util.Collection;
import java.util.List;

public class RndCollection {

    public static <T> T random(List<T> collection) {
        return collection.get(Rnd.get(collection.size()));
    }

}
