package com.es.phoneshop.utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class InputValidator {

    public static boolean isInvalidQuantity(String quantityStr) {
        return !quantityStr.matches("[\\d.,]+") || quantityStr.startsWith("0");
    }

    public static int parseQuantity(String quantityStr, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(quantityStr).intValue();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
