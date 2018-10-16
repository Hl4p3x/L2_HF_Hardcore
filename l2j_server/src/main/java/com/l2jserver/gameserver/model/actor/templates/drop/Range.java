package com.l2jserver.gameserver.model.actor.templates.drop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.l2jserver.util.Rnd;

import java.util.Objects;

public class Range {

    private int low;
    private int high;

    public Range() {
    }

    public Range(int value) {
        this.low = value;
        this.high = value;
    }

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

    public int randomWithin() {
        return Rnd.get(low, high);
    }

    public boolean equalsTo(int single) {
        return low == single && high == single;
    }

    public boolean isPositive() {
        return high - low >= 0;
    }

    @JsonCreator
    public static Range fromString(String raw) {
        String[] parts = raw.split("-");
        if (parts.length == 2) {
            return new Range(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            int singleValue = Integer.parseInt(raw);
            return new Range(singleValue, singleValue);
        }
    }

    @JsonValue
    public String asString() {
        if (low == high) {
            return String.valueOf(low);
        } else {
            return low + "-" + high;
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
        return asString();
    }

}
