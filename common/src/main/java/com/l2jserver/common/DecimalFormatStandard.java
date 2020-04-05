package com.l2jserver.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class DecimalFormatStandard {

    public static DecimalFormat moneyFormat() {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter;
    }
}
