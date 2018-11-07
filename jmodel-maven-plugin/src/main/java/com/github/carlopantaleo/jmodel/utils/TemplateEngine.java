package com.github.carlopantaleo.jmodel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class TemplateEngine {
    private JtwigTemplate template;
    private JtwigModel model = JtwigModel.newModel();

    public TemplateEngine(String template) {
        this.template = JtwigTemplate.inlineTemplate(template);
    }

    public TemplateEngine addField(String name, Object value) {
        model.with(name, value);
        return this;
    }

    public TemplateEngine addIteratedField(IteratedField iteratedField) {
        if (!iteratedField.fields.isEmpty()) {
            iteratedField.complete();
        }

        model.with(iteratedField.name, iteratedField.list);
        return this;
    }

    public String compile() {
        return template.render(model);
    }

    public static class IteratedField {
        private Map<String, Object> fields = new HashMap<>();
        private List<Map<String, Object>> list = new ArrayList<>();

        private final String name;

        public IteratedField(String name) {
            this.name = name;
        }

        public IteratedField addField(String name, Object value) {
            fields.put(name, value);
            return this;
        }

        public IteratedField next() {
            if (!fields.isEmpty()) {
                list.add(fields);
                fields = new HashMap<>();
            }
            return this;
        }

        public IteratedField complete() {
            return next();
        }
    }
}
