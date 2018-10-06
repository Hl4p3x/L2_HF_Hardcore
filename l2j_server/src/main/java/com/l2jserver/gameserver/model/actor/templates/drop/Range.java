package com.l2jserver.gameserver.model.actor.templates.drop;

import java.util.Objects;

public class Range {

    private int low;
    private int high;

    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    public static Range from(String raw) {
        String[] parts = raw.split("-");
        if (parts.length == 2) {
            return new Range(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            int singleValue = Integer.parseInt(raw);
            return new Range(singleValue, singleValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return low == range.low &&
                high == range.high;
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high);
    }

    @Override
    public String toString() {
        if (low == high) {
            return String.valueOf(low);
        } else {
            return String.format("%s-%s", low, high);
        }
    }

}
