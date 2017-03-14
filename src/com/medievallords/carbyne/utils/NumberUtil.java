package com.medievallords.carbyne.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtil {

    static DecimalFormat twoDPlaces = new DecimalFormat("#,###.##");
    static DecimalFormat currencyFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public static String formatDouble(final double value) {
        twoDPlaces.setRoundingMode(RoundingMode.HALF_UP);
        return twoDPlaces.format(value);
    }

    public static String formatAsCurrency(final BigDecimal value) {
        currencyFormat.setRoundingMode(RoundingMode.FLOOR);
        String str = currencyFormat.format(value);
        if (str.endsWith(".00")) {
            str = str.substring(0, str.length() - 3);
        }
        return str;
    }

    public static boolean isInt(final String sInt) {
        try {
            Integer.parseInt(sInt);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
