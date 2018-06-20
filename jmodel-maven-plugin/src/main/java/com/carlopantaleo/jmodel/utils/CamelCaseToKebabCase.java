package com.carlopantaleo.jmodel.utils;

public class CamelCaseToKebabCase {
    private final String input;

    public CamelCaseToKebabCase(String input) {
        this.input = input;
    }

    public String convert() {
        return input.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    public static String toKebabCase(String input) {
        return new CamelCaseToKebabCase(input).convert();
    }
}
