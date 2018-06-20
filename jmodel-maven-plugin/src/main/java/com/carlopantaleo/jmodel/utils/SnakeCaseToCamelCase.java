package com.carlopantaleo.jmodel.utils;

import com.carlopantaleo.jmodel.exceptions.ValidationException;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

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
    private static final String VALIDATION_PATTERN = "^(?!_)[A-Za-z0-9_]*";

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
    public static String toCamelCase(String input) throws ValidationException {
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
    public static String toCamelCaseCapital(String input) throws ValidationException {
        return new SnakeCaseToCamelCase(input, false).convert();
    }

    public String convert() throws ValidationException {
        validateInput();

        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = processLetter(i);
            if (c != 0) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private char processLetter(int i) {
        if (i == 0) {
            return lowerFirstLetter ? toLowerCase(input.charAt(i)) : toUpperCase(input.charAt(i));
        }

        if (input.charAt(i - 1) == '_') {
            return Character.toUpperCase(input.charAt(i));
        }

        if (input.charAt(i) == '_') {
            return 0;
        }

        return toLowerCase(input.charAt(i));
    }

    private void validateInput() throws ValidationException {
        if (!input.matches(VALIDATION_PATTERN)) {
            throw new ValidationException(input, VALIDATION_PATTERN);
        }
    }
}
