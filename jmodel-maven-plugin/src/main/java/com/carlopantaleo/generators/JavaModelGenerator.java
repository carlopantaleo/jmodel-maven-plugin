package com.carlopantaleo.generators;

import com.carlopantaleo.entities.Enum;
import com.carlopantaleo.entities.Field;
import com.carlopantaleo.entities.Table;
import com.carlopantaleo.exceptions.ValidationException;
import com.carlopantaleo.utils.EntitesExtractor;
import com.carlopantaleo.utils.SnakeCaseToCamelcase;
import com.google.common.collect.Sets;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JavaModelGenerator {
    private final Set<String> possibleImports = Sets.newHashSet(
            "javax.annotation.Nullable",
            "java.math.BigDecimal"
    );

    private final XPath xPath = XPathFactory.newInstance().newXPath();
    private String destinationPackage;
    private Document model;
    private String projectDir;

    public JavaModelGenerator(String destinationPackage, Document model, String projectDir) {
        this.destinationPackage = destinationPackage;
        this.model = model;
        this.projectDir = projectDir;
    }

    public void generateSources() throws Exception {
        validateInput();

        EntitesExtractor extractor = new EntitesExtractor(model);

        for (Table table : extractor.extractTablesAndFields()) {
            writeTableJavaFile(table);
        }

        for (Enum theEnum : extractor.extractEnumsAndItems()) {
            writeEnumJavaFile(theEnum);
        }
    }

    private void writeTableJavaFile(Table table) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(makeDestinationPath() +
                SnakeCaseToCamelcase.toCamelCaseCapital(table.getName()) + ".java")) {

            String source = makeSource(table);
            fos.write(source.getBytes("UTF8"));
        }
    }

    private String makeDestinationPath() throws MojoFailureException {
        String destinationPath =
                projectDir + "/src/main/java/" + destinationPackage.replace('.', '/') + "/";

        try {
            Files.createDirectories(new File(destinationPath).toPath());
        } catch (Exception e) {
            throw new MojoFailureException("Unable to create directory tree " + destinationPath, e);
        }

        return destinationPath;
    }

    private void writeEnumJavaFile(Enum theEnum) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(projectDir + "/src/main/java/" +
                destinationPackage.replace('.', '/') + "/" +
                SnakeCaseToCamelcase.toCamelCaseCapital(theEnum.getName()) + ".java")) {

            String source = makeSource(theEnum);
            fos.write(source.getBytes("UTF8"));
        }
    }

    private String makeSource(Table table) throws ValidationException, FormatterException {
        StringBuilder sb = new StringBuilder();

        for (Field field : table.getFields()) {
            writeField(sb, field);
        }

        for (Field field : table.getFields()) {
            writeGetterAndSetter(sb, field);
        }

        writeHeaderAndFooter(table, sb);
        return beautify(sb);
    }

    private String makeSource(Enum theEnum) throws ValidationException, FormatterException {
        StringBuilder sb = new StringBuilder();

        // Package and enum declaration
        sb.append("package ").append(destinationPackage).append(';');
        sb.append("public enum ").append(SnakeCaseToCamelcase.toCamelCaseCapital(theEnum.getName()))
                .append('{');

        // Enum items
        for (String item : theEnum.getItems()) {
            sb.append(item).append(',');
        }
        sb.deleteCharAt(sb.length() - 1); // Remove last comma

        // Footer
        sb.append('}');

        return beautify(sb);
    }

    private void writeGetterAndSetter(StringBuilder sb, Field field) throws ValidationException {
        String nameCapital = SnakeCaseToCamelcase.toCamelCaseCapital(field.getName());
        String name = SnakeCaseToCamelcase.toCamelCase(field.getName());

        // Getter
        sb.append("public ");
        appendNullable(sb, field);
        appendType(sb, field);
        sb.append("get").append(nameCapital).append("(){")
                .append("return ").append(name).append(';')
                .append('}');

        // Setter
        sb.append("public void set").append(nameCapital)
                .append('(');
        appendNullable(sb, field);
        appendType(sb, field);
        sb.append(name).append("){")
                .append("this.").append(name).append('=').append(name).append(';')
                .append('}');
    }

    private void appendNullable(StringBuilder sb, Field field) {
        if (field.isNullable()) {
            sb.append("@Nullable ");
        }
    }

    private String beautify(StringBuilder sb) throws FormatterException {
        return new Formatter(
                JavaFormatterOptions.builder()
                        .style(JavaFormatterOptions.Style.AOSP)
                        .build())
                .formatSource(sb.toString());
    }

    private void writeField(StringBuilder sb, Field field) throws ValidationException {
        // Qualifier
        sb.append("private ");

        // Annotations
        appendNullable(sb, field);

        // Type
        appendType(sb, field);

        // Name
        sb.append(SnakeCaseToCamelcase.toCamelCase(field.getName()))
                .append(';');
    }

    private void appendType(StringBuilder sb, Field field) throws ValidationException {
        Class type = field.getType();
        if (type == Long.class || type == Double.class || type == Float.class || type == Boolean.class) {
            sb.append(type.getSimpleName().toLowerCase());
        } else if (type == Enum.class) {
            String enumName = SnakeCaseToCamelcase.toCamelCaseCapital(field.getReferredEnum());
            sb.append(enumName);
        } else {
            sb.append(type.getSimpleName());
        }
        sb.append(' ');
    }

    private void writeHeaderAndFooter(Table table, StringBuilder sb) throws ValidationException {
        StringBuilder sbHead = generateHeading(sb.toString());
        sbHead.append("public class ")
                .append(SnakeCaseToCamelcase.toCamelCaseCapital(table.getName()))
                .append('{');

        // Insert heading into main StringBuilder
        sb.insert(0, sbHead);

        // Footer
        sb.append('}');
    }

    private StringBuilder generateHeading(String body) {
        StringBuilder sbHead = new StringBuilder();
        sbHead.append("package ").append(destinationPackage).append(';');

        for (String possibleImport : possibleImports) {
            String unqualifiedName = possibleImport.substring(possibleImport.lastIndexOf('.') + 1);
            if (body.contains(unqualifiedName)) {
                sbHead.append("import ")
                        .append(possibleImport)
                        .append(';');
            }
        }

        return sbHead;
    }

    private void validateInput() throws ValidationException {
        if (destinationPackage == null) {
            throw new ValidationException("destinationPackage cannot be null.");
        }
        String pattern = "^(?!\\.)[a-z.]*[a-z]$";
        if (!destinationPackage.matches(pattern)) {
            throw new ValidationException(destinationPackage, pattern);
        }
        if (model == null) {
            throw new ValidationException("model cannot be null.");
        }
    }
}
