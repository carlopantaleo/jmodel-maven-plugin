package com.carlopantaleo.jmodel.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCaseToKebabCaseTest {
    @Test
    public void toKebabCase_works1() {
        String input = "camelCasedWord";
        String output = CamelCaseToKebabCase.toKebabCase(input);
        assertEquals("camel-cased-word", output);
    }

    @Test
    public void toKebabCase_works2() {
        String input = "CamelCasedWord";
        String output = CamelCaseToKebabCase.toKebabCase(input);
        assertEquals("camel-cased-word", output);
    }

    @Test
    public void toKebabCase_works3() {
        String input = "CamelCASEDWord";
        String output = CamelCaseToKebabCase.toKebabCase(input);
        assertEquals("camel-casedword", output);
    }
}