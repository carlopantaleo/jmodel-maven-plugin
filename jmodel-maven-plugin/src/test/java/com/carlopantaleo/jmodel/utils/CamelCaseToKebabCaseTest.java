package com.carlopantaleo.jmodel.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCaseToKebabCaseTest {
    @Test
    public void toKebabCase_works() {
        String input = "camelCasedWord";
        String output = CamelCaseToKebabCase.toKebabCase(input);
        assertEquals("camel-cased-word", output);
    }
}