package com.carlopantaleo.jmodel.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class JavascriptBeautifierForJavaTest {
    @Test
    public void beautify_works() throws Exception {
        String unformattedJs = "var a = 1; b = 2; var user = { name : \n \"Andrew\"}";
        String expectedJs = "var a = 1;\nb = 2;\nvar user = {\n    name: \"Andrew\"\n}";

        JavascriptBeautifierForJava javascriptBeautifierForJava = new JavascriptBeautifierForJava();
        String formattedJs = javascriptBeautifierForJava.beautify(unformattedJs);
        assertEquals(expectedJs, formattedJs);
    }
}