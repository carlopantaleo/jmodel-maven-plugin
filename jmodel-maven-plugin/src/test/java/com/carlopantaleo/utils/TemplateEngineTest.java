package com.carlopantaleo.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateEngineTest {
    private final String template =
            "MyField: &{my-field}\n" +
                    "An Integer Field: &{int-field}\n" +
                    "@iterated(iter)[" +
                    "Iterated-1: &{one}\n" +
                    "Iterated-2: &{two}\n" +
                    "&{undef-inner-field}" +
                    "]" +
                    "@ifdef(defined)[" +
                    "@iterated(iteratedDef)[" +
                    "Iterated-3: &{three}\n]" +
                    "]" +
                    "@ifdef(undefined)[\n" +
                    "   Nothing\n" +
                    "]" +
                    "@@escaped\n" +
                    "&&escaped" +
                    "&{undef-field}";
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
        TemplateEngine.IteratedField ifield2 = new TemplateEngine.IteratedField("iteratedDef")
                .addField("three", "cinque")
                .next()
                .addField("three", "sei")
                .complete();
        te.addIteratedField(ifield2);
        te.addField("defined", true);

        assertEquals("MyField: hello\n" +
                "An Integer Field: 4\n" +
                "Iterated-1: uno\n" +
                "Iterated-2: due\n" +
                "Iterated-1: tre\n" +
                "Iterated-2: quattro\n" +
                "Iterated-3: cinque\n" +
                "Iterated-3: sei\n" +
                "@@escaped\n" +
                "&&escaped", te.compile());
    }

}