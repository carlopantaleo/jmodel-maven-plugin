package com.carlopantaleo.jmodel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {
    private String template;
    private String result;
    private Map<String, Object> fields = new HashMap<>();
    private Map<String, List<Map<String, Object>>> iteratedFields = new HashMap<>();

    public TemplateEngine(String template) {
        this.template = template;
    }

    public TemplateEngine addField(String name, Object value) {
        fields.put(name, value);
        return this;
    }

    public TemplateEngine addIteratedField(IteratedField iteratedField) {
        if (!iteratedField.fields.isEmpty()) {
            iteratedField.complete();
        }

        iteratedFields.put(iteratedField.name, iteratedField.list);
        return this;
    }

    public String compile() {
        result = escapeDollarSign(template);
        result = replaceIteratedFields(iteratedFields, result);
        result = replaceConditionalFields(fields, result);
        result = replacePlainFields(fields, result);
        return result;
    }

    private String escapeDollarSign(String template) {
        return template.replace("$", "\\$");
    }

    private String replaceConditionalFields(Map<String, Object> fields, String template) {
        String result = template;
        String regexIfDef = "(?<!@)@ifdef\\(([-a-zA-Z0-9]*)\\)\\[(.*?)]";
        Pattern pattern = Pattern.compile(regexIfDef, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(template);

        while (matcher.find()) {
            if (matcher.groupCount() == 2) {
                String varName = matcher.group(1);
                String regexReplace = "(?<!@)@ifdef\\(" + varName + "\\)\\[(.*?)]";
                Matcher replacer = Pattern.compile(regexReplace, Pattern.DOTALL).matcher(result);

                if (fields.get(varName) != null) {
                    String partialResult = matcher.group(2);
                    result = replacer.replaceAll(partialResult);
                } else {
                    result = replacer.replaceAll("");
                }
            }
        }

        return result;
    }


    private String replacePlainFields(Map<String, Object> fields, String template) {
        String result = template;
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            result = result.replaceAll("(?<!&)&\\{" + entry.getKey() + "}", entry.getValue().toString());
        }

        return replaceUndefinedPlainFields(result);
    }

    private String replaceUndefinedPlainFields(String result) {
        return result.replaceAll("(?<!&)&\\{[-a-zA-Z0-9]*}", "");
    }

    private String replaceIteratedFields(Map<String, List<Map<String, Object>>> iteratedFields, String template) {
        String result = template;
        for (Map.Entry<String, List<Map<String, Object>>> entry : iteratedFields.entrySet()) {
            String regex = "(?<!@)@iterated\\(" + entry.getKey() + "\\)\\[(.*?)]";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(result);

            while (matcher.find() && matcher.groupCount() == 1) {
                String partialTemplate = matcher.group(1);
                StringBuilder partialResult = new StringBuilder();
                for (Map<String, Object> fields : entry.getValue()) {
                    partialResult.append(replacePlainFields(fields, partialTemplate));
                }

                result = pattern.matcher(result).replaceAll(partialResult.toString());
            }
        }

        return replaceUndefinedIteratedFields(result);
    }

    private String replaceUndefinedIteratedFields(String result) {
        String regex = "(?<!@)@iterated\\([-a-zA-Z0-9]*\\)\\[(.*?)]";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(result);
        return matcher.replaceAll("");
    }

    public static class IteratedField {
        private Map<String, Object> fields = new HashMap<>();
        private List<Map<String, Object>> list = new ArrayList<>();

        private final String name;

        public IteratedField(String name) {
            this.name = name;
        }

        public IteratedField addField(String name, String value) {
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
