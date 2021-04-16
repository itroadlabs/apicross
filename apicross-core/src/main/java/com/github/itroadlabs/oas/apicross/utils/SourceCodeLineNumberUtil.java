package com.github.itroadlabs.oas.apicross.utils;

import static java.lang.String.format;

public class SourceCodeLineNumberUtil {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";

    public static String addLineNumbers(String source) {
        String[] lines = source.split("\n");
        StringBuilder builder = new StringBuilder();
        int lineNumber = 0;
        for (String line : lines) {
            builder.append(GREEN).append(format("%4d: ", ++lineNumber)).append(RESET).append(line).append("\n");
        }
        return builder.toString();
    }
}
