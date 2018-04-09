package com.carlopantaleo.jmodel.exceptions;

/**
 * Generic exception thrown when validation operations fail.
 */
public class ValidationException extends Exception {
    /**
     * Use this constructor to specify the {@code value} for which validation failed against the {@code pattern}.
     * @param value the value to validate.
     * @param pattern the validation pattern. May be a regular expression or simply a sentence.
     */
    public ValidationException(String value, String pattern) {
        super(String.format("Validation of value '%s' failed against pattern /%s/", value, pattern));
    }

    public ValidationException(String message) {
        super(message);
    }
}
