package com.github.carlopantaleo.jmodel.utils;

import com.google.common.base.CaseFormat;

/**
 * Converts a {@code SNAKE_CASE} string to a {@code camelCase} one.
 * <p>Example usage:
 * <pre>
 *  String converted = SnakeCaseToCamelCase.convert("SOME_STRING");
 *  assert converted.equals("someString");
 *
 *  String converted2 = SnakeCaseToCamelCase.convert("SOME_OTHER_STRING", false);
 *  assert converted2.equals("SomeOtherString");
 * </pre>
 *
 */
public class SnakeCaseToCamelCase {
    private final String input;
    private boolean lowerFirstLetter = true;

    private SnakeCaseToCamelCase(String input) {
        this.input = input;
    }

    private SnakeCaseToCamelCase(String input, boolean lowerFirstLetter) {
        this.input = input;
        this.lowerFirstLetter = lowerFirstLetter;
    }

    /**
     * Converts a {@code SNAKE_CASE} string to a {@code camelCase} one, leaving the first letter lower case.
     * <p>
     * Example usage:
     * <pre>
     *  String converted = SnakeCaseToCamelCase.convert("SOME_STRING");
     *  assert converted.equals("someString");
     * </pre>
     *
     * @param input the input string.
     * @return the converted string.
     */
    public static String toCamelCase(String input) {
        return new SnakeCaseToCamelCase(input).convert();
    }

    /**
     * Converts a {@code SNAKE_CASE} string to a {@code CamelCase} one, with capital letter.
     * <p>
     * Example usage:
     * <pre>
     *  String converted = SnakeCaseToCamelCase.convert("SOME_STRING");
     *  assert converted.equals("SomeString");
     * </pre>
     *
     * @param input the input string.
     * @return the converted string.
     */
    public static String toCamelCaseCapital(String input) {
        return new SnakeCaseToCamelCase(input, false).convert();
    }

    public String convert() {
        String output = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, input);
        return lowerFirstLetter ? output.substring(0, 1).toLowerCase() + output.substring(1) : output;
    }
}
