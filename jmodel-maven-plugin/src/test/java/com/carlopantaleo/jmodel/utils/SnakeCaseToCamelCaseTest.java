package com.carlopantaleo.jmodel.utils;

import com.carlopantaleo.jmodel.exceptions.ValidationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SnakeCaseToCamelCaseTest {
    @Test
    public void convert_worksOnFirstLowerCase() throws ValidationException {
        String converted = SnakeCaseToCamelCase.toCamelCase("SOME_TEXT_TO_CONVERT");
        assertEquals("someTextToConvert", converted);
    }

    @Test
    public void convert_worksOnFirstLUpperCase() throws ValidationException {
        String converted = SnakeCaseToCamelCase.toCamelCaseCapital("SOME_TEXT_TO_CONVERT");
        assertEquals("SomeTextToConvert", converted);
    }

    @Test
    public void convert_worksOnMixedCase() throws ValidationException {
        String converted = SnakeCaseToCamelCase.toCamelCaseCapital("some_textTo_coNvert");
        assertEquals("SomeTexttoConvert", converted);
    }

    @Test
    public void convert_worksOnDirtyInput() throws ValidationException {
        String converted = SnakeCaseToCamelCase.toCamelCase("_SOME_TEXT_TO_CONVERT");
        assertEquals("someTextToConvert", converted);
    }
}