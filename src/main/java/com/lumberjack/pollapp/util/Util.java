package com.lumberjack.pollapp.util;

import java.text.DecimalFormat;

public class Util {

    public static final DecimalFormat df = new DecimalFormat("0.00");

    public static double convertToDecimalWithTwoPlaces(double value) {
        return Double.parseDouble(df.format(value));
    }
}
