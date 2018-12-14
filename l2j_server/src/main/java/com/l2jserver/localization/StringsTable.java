package com.l2jserver.localization;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class StringsTable {

    private Map<String, String> strings;

    public StringsTable(Map<String, String> strings) {
        this.strings = strings;
    }

    public String get(String code) {
        return strings.get(code);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringsTable.class.getSimpleName() + "[", "]")
                .add(Objects.toString(strings))
                .toString();
    }

}
