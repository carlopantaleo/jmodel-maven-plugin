package com.github.carlopantaleo.jmodel.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateEngineTest {
    private final String template =
            "MyField: {{ myField }}\n" +
                    "An Integer Field: {{ intField }}\n" +
                    "{% for item in iter %}" +
                    "Iterated-1: {{ item.one }}\n" +
                    "Iterated-2: {{ item.two }}\n" +
                    "{% endfor %}" +
                    "{% if defined(def) %}" +
                    "{% for item in iteratedDef %}" +
                    "Iterated-3: {{ item.three }}\n" +
                    "{% endfor %}" +
                    "{% endif %}" +
                    "{% if defined(undef) %}\n" +
                    "   Nothing\n" +
                    "{% endif %}" +
                    "@@escaped\n" +
                    "&&escaped";

    @Test
    public void templateEngineWorks() {
        TemplateEngine te = new TemplateEngine(template);
        te.addField("myField", "hello")
                .addField("intField", 4);
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
        te.addField("def", true);

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