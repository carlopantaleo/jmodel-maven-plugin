package com.github.carlopantaleo.jmodel.generators;

import com.github.carlopantaleo.jmodel.entities.Enum;
import com.github.carlopantaleo.jmodel.entities.Field;
import com.github.carlopantaleo.jmodel.entities.Table;
import com.github.carlopantaleo.jmodel.exceptions.ValidationException;
import com.github.carlopantaleo.jmodel.utils.CamelCaseToKebabCase;
import com.github.carlopantaleo.jmodel.utils.EntitesExtractor;
import com.github.carlopantaleo.jmodel.utils.SnakeCaseToCamelCase;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TypescriptModelGenerator {
    private static final String TS_EXTENSION = ".ts";
    private static final String TAB = "    ";

    private String destinationDir;
    private Document model;
    private String projectDir;

    public TypescriptModelGenerator(String destinationDir, Document model, String projectDir) {
        this.destinationDir = destinationDir;
        this.model = model;
        this.projectDir = projectDir;
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

    private String makeSource(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("export class ")
                .append(table.getClassName())
                .append(" {\n");

        Set<String> imports = new HashSet<>();
        for (Field field : table.getFields()) {
            sb.append(TAB);
            writeField(sb, field, imports);
            sb.append('\n');
        }

        sb.append('}');

        String partialOutput = sb.toString();

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

    private String makeSource(Enum theEnum) {
        StringBuilder sb = new StringBuilder();
        sb.append("export enum ")
                .append(SnakeCaseToCamelCase.toCamelCaseCapital(theEnum.getName()))
                .append(" {\n");

        for (String item : theEnum.getItems()) {
            sb.append(TAB);
            sb.append(item).append(",\n");
        }
        sb.deleteCharAt(sb.length() - 2); // Remove last comma

        sb.append('}');

        return sb.toString();
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
