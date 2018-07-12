package com.l2jserver.util;

public enum Grade {

    NG("NG"), D("D"), C("C"), B("B"), A("A"), S("S"), S_PLUS("S+"), UNKNOWN("Unknown");

    private final String symbol;

    Grade(String symbol) {
        this.symbol = symbol;
    }

    public String gradeSymbol() {
        return symbol;
    }

}
