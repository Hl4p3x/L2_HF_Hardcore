package com.l2jserver.util;

public class SequenceGenerator {

    private int value = 0;

    public int next() {
        value = value + 1;
        return value;
    }

    public int current() {
        return value;
    }

}
