package com.carlopantaleo.jmodel.generators;

import com.carlopantaleo.jmodel.entities.Enum;
import com.carlopantaleo.jmodel.entities.Field;
import com.carlopantaleo.jmodel.entities.Table;
import com.carlopantaleo.jmodel.exceptions.ValidationException;
import com.carlopantaleo.jmodel.utils.CamelCaseToKebabCase;
import com.carlopantaleo.jmodel.utils.EntitesExtractor;
import com.carlopantaleo.jmodel.utils.JavascriptBeautifierForJava;
import com.carlopantaleo.jmodel.utils.SnakeCaseToCamelCase;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TypescriptModelGenerator {
    private static final String TS_EXTENSION = ".ts";

    private String destinationDir;
    private Document model;
    private String projectDir;

    private final JavascriptBeautifierForJava beautifier;

    public TypescriptModelGenerator(String destinationDir, Document model, String projectDir)
            throws MojoFailureException {
        this.destinationDir = destinationDir;
        this.model = model;
        this.projectDir = projectDir;

        try {
            beautifier = new JavascriptBeautifierForJava();
        } catch (Exception e) {
            throw new MojoFailureException("Unable to initialize javascript beautifier", e);
        }
    }

    public void generateSources() throws Exception {
        validateInput();

        EntitesExtractor extractor = new EntitesExtractor(model);

        for (Table table : extractor.extractTablesAndFields()) {
            writeTableTypescriptFile(table);
        }

        for (Enum theEnum : extractor.extractEnumsAndItems()) {
            writeEnumTypescriptFile(theEnum);
        }
    }

    private void writeTableTypescriptFile(Table table) throws Exception {
        String className = table.getClassName();

        try (FileOutputStream fos = new FileOutputStream(makeDestinationPath() +
                CamelCaseToKebabCase.toKebabCase(className) + TS_EXTENSION)) {
            String source = makeSource(table);
            fos.write(source.getBytes("UTF8"));
        }
    }

    private void writeEnumTypescriptFile(Enum theEnum) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(makeDestinationPath() +
                CamelCaseToKebabCase.toKebabCase(
                        SnakeCaseToCamelCase.toCamelCase(theEnum.getName())) + TS_EXTENSION)) {

            String source = makeSource(theEnum);
            fos.write(source.getBytes("UTF8"));
        }
    }

    private String makeDestinationPath() throws MojoFailureException {
        String destinationPath =
                projectDir + "/" + destinationDir + "/";

        try {
            Files.createDirectories(new File(destinationPath).toPath());
        } catch (Exception e) {
            throw new MojoFailureException("Unable to create directory tree " + destinationPath, e);
        }

        return destinationPath;
    }

    private String makeSource(Table table) throws MojoExecutionException {
        StringBuilder sb = new StringBuilder();
        sb.append("export class ")
                .append(table.getClassName())
                .append('{');

        Set<String> imports = new HashSet<>();
        for (Field field : table.getFields()) {
            writeField(sb, field, imports);
        }

        sb.append('}');

        String partialOutput = beautify(sb);

        sb = new StringBuilder();
        writeImports(sb, imports);
        sb.append(partialOutput);

        return sb.toString();
    }

    private void writeImports(StringBuilder sb, Set<String> imports) {
        if (!imports.isEmpty()) {
            for (String anImport : imports) {
                sb.append("import {").append(anImport).append("}")
                        .append(" from '")
                        .append(CamelCaseToKebabCase.toKebabCase(anImport)).append(TS_EXTENSION).append("';\n");
            }

            sb.append('\n');
        }
    }

    private String beautify(StringBuilder sb) throws MojoExecutionException {
        try {
            return beautifier.beautify(sb.toString());
        } catch (ScriptException | NoSuchMethodException e) {
            throw new MojoExecutionException("Error while beautifying generated javascript", e);
        }
    }

    private void writeField(StringBuilder sb, Field field, Set<String> imports) {
        sb.append(SnakeCaseToCamelCase.toCamelCase(field.getName()));
        appendType(sb, field, imports);
    }

    private void appendType(StringBuilder sb, Field field, Set<String> imports) {
        sb.append(": ");

        Class type = field.getType();
        if (type == Long.class || type == Double.class || type == Float.class || type == BigDecimal.class) {
            sb.append("number");
        } else if (type == Boolean.class || type == String.class) {
            sb.append(type.getSimpleName().toLowerCase());
        } else if (type == LocalDateTime.class) {
            sb.append("Date");
        } else if (type == Enum.class) {
            String enumName = SnakeCaseToCamelCase.toCamelCaseCapital(field.getReferredEnum());
            sb.append(enumName);
            imports.add(enumName);
        } else {
            sb.append(type.getSimpleName());
            imports.add(type.getSimpleName());
        }

        sb.append(';');
    }

    private String makeSource(Enum theEnum) throws MojoExecutionException {
        StringBuilder sb = new StringBuilder();
        sb.append("export enum ")
                .append(SnakeCaseToCamelCase.toCamelCaseCapital(theEnum.getName()))
                .append('{');

        for (String item : theEnum.getItems()) {
            sb.append(item).append(',');
        }
        sb.deleteCharAt(sb.length() - 1); // Remove last comma

        sb.append('}');

        return beautify(sb);
    }

    private void validateInput() throws ValidationException {
        if (destinationDir == null) {
            throw new ValidationException("destinationDir cannot be null.");
        }
        String pattern = "^[a-z/]*$";
        if (!destinationDir.matches(pattern)) {
            throw new ValidationException(destinationDir, pattern);
        }
        if (model == null) {
            throw new ValidationException("model cannot be null.");
        }
    }
}