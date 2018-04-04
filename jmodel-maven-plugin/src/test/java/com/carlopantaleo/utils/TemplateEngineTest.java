package com.carlopantaleo.utils;

import com.carlopantaleo.utils.TemplateEngine;
import org.junit.Test;

import static org.junit.Assert.*;

public class TemplateEngineTest {
    private final String template =
            "MyField: ${my-field}\n" +
                    "An Integer Field: ${int-field}\n" +
                    "@iterated(iter)[" +
                    "Iterated-1: ${one}\n" +
                    "Iterated-2: ${two}\n" +
                    "]";
    @Test
    public void templateEngineWorks() throws Exception {
        TemplateEngine te = new TemplateEngine(template);
        te.addField("my-field", "hello")
                .addField("int-field", 4);
        TemplateEngine.IteratedField ifield = new TemplateEngine.IteratedField("iter")
                .addField("one", "uno")
                .addField("two", "due")
                .next()
                .addField("one", "tre")
                .addField("two", "quattro")
                .complete();
        te.addIteratedField(ifield);

        assertEquals("MyField: hello\n" +
                "An Integer Field: 4\n" +
                "Iterated-1: uno\n" +
                "Iterated-2: due\n" +
                "Iterated-1: tre\n" +
                "Iterated-2: quattro\n", te.compile());
    }

}