package com.github.carlopantaleo.jmodel.utils;

import com.google.common.base.CaseFormat;

/**
 * <p>Converts a {@code SNAKE_CASE} string to a {@code camelCase} one.</p> <p>Example usage:
 * <pre>
 *     String converted = SnakeCaseToCamelCase.convert("SOME_STRING");
 *     assert converted.equals("someString");
 *
 *     String converted2 = SnakeCaseToCamelCase.convert("SOME_OTHER_STRING", false);
 *     assert converted2.equals("SomeOtherString");
 * </pre>
 * </p>
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
     * <p>Converts a {@code SNAKE_CASE} string to a {@code camelCase} one, leaving the first letter lower case.</p>
     * <p>Example usage:
     * <pre>
     *     String converted = SnakeCaseToCamelCase.convert("SOME_STRING");
     *     assert converted.equals("someString");
     * </pre>
     * </p>
     *
     * @param input the input string.
     */
    public static String toCamelCase(String input) {
        return new SnakeCaseToCamelCase(input).convert();
    }

    /**
     * <p>Converts a {@code SNAKE_CASE} string to a {@code CamelCase} one, with capital letter.</p>
     * <p>Example usage:
     * <pre>
     *     String converted = SnakeCaseToCamelCase.convert("SOME_STRING");
     *     assert converted.equals("SomeString");
     * </pre>
     * </p>
     *
     * @param input            the input string.
     */
    public static String toCamelCaseCapital(String input) {
        return new SnakeCaseToCamelCase(input, false).convert();
    }

    public String convert() {
        String output = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, input);
        return lowerFirstLetter ? output.substring(0, 1).toLowerCase() + output.substring(1) : output;
    }
}
