package com.github.carlopantaleo.jmodel.generators;

import com.github.carlopantaleo.jmodel.entities.Enum;
import com.github.carlopantaleo.jmodel.entities.Field;
import com.github.carlopantaleo.jmodel.entities.Table;
import com.github.carlopantaleo.jmodel.exceptions.ValidationException;
import com.github.carlopantaleo.jmodel.utils.EntitesExtractor;
import com.github.carlopantaleo.jmodel.utils.SharedConstants;
import com.github.carlopantaleo.jmodel.utils.SnakeCaseToCamelCase;
import com.github.carlopantaleo.jmodel.utils.TemplateEngine;
import com.github.carlopantaleo.jmodel.utils.TemplateEngine.IteratedField;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HibernateMappingsGenerator {
    private static final String HBM_TEMPLATE = "hbm/hbm-xml-template.twig";

    private final String destinationDaoPackage;
    private final String beansPackage;
    private final Document model;
    private final String projectDir;
    private final String destinationResourceDir;

    public HibernateMappingsGenerator(String destinationDaoPackage,
                                      String beansPackage,
                                      String destinationResourceDir,
                                      Document model,
                                      String projectDir) {
        this.destinationDaoPackage = destinationDaoPackage;
        this.beansPackage = beansPackage;
        this.model = model;
        this.projectDir = projectDir;
        this.destinationResourceDir = destinationResourceDir;
    }

    public void generateSources() throws Exception {
        validateInput();

        EntitesExtractor extractor = new EntitesExtractor(model);

        for (Table table : extractor.extractTablesAndFields()) {
            writeHibernateMappingFile(table);
        }
    }

    private void writeHibernateMappingFile(Table table) throws MojoFailureException {
        URL url = Resources.getResource(HBM_TEMPLATE);
        try {
            String template = Resources.toString(url, Charsets.UTF_8);
            TemplateEngine templateEngine = buildTemplateEngine(template, table);
            writeHbmFile(templateEngine.compile(), table);
        } catch (IOException e) {
            throw new MojoFailureException("Unable to open resource.", e);
        }
    }

    private void writeHbmFile(String result, Table table) throws IOException, MojoFailureException {
        String destinationDir = projectDir + "/src/main/resources/" + destinationResourceDir;
        try {
            Files.createDirectories(new File(destinationDir).toPath());
        } catch (Exception e) {
            throw new MojoFailureException("Unable to create directory tree " + destinationDir, e);
        }

        String mappingName = table.getClassName();
        String filePath = destinationDir + "/" + mappingName + ".hbm.xml";
        File outFile = new File(filePath);
        Files.write(outFile.toPath(), result.getBytes());
    }

    private TemplateEngine buildTemplateEngine(String template, Table table) {
        TemplateEngine templateEngine = new TemplateEngine(template)
                .addField("autogenWarn", SharedConstants.AUTOGEN_WARN)
                .addField("qualifiedClassName", beansPackage + "." + table.getClassName())
                .addField("tableName", table.getName());

        List<Field> pk = addAndGetPk(table, templateEngine);
        IteratedField iteratedField = addAndGetIteratedFields(table, pk);

        templateEngine.addIteratedField(iteratedField);
        return templateEngine;
    }

    private IteratedField addAndGetIteratedFields(Table table, List<Field> pk) {
        IteratedField iteratedField = new IteratedField("fields");
        for (Field field : table.getFields()) {
            if (pk.contains(field)) {
                continue;
            }

            addFieldToIteratedField(iteratedField, field);
        }
        return iteratedField;
    }

    private void addFieldToIteratedField(IteratedField iteratedField, Field field) {
        iteratedField.addField("fieldName", field.getName())
                .addField("fieldName", SnakeCaseToCamelCase.toCamelCase(field.getName()))
                .addField("fieldColumnName", field.getName());
        addFieldType(iteratedField, field);
        iteratedField.next();
    }

    private List<Field> addAndGetPk(Table table, TemplateEngine templateEngine) {
        List<Field> pk = table.getPk();

        if (pk.size() == 1) {
            Field id = Iterables.getFirst(pk, null);
            templateEngine.addField("pkFieldName", SnakeCaseToCamelCase.toCamelCase(
                    Objects.requireNonNull(id, "pk must have been not null at this point.").getName()));
            templateEngine.addField("pkFieldColumnName", id.getName());
            templateEngine.addField("singleId", true);
            addFieldType(templateEngine, id);
        } else {
            IteratedField iteratedField = new IteratedField("pkFields");
            for (Field field : pk) {
                addFieldToIteratedField(iteratedField, field);
            }

            templateEngine.addField("compositeId", true);
            templateEngine.addIteratedField(iteratedField);
        }

        return pk;
    }

    private void addFieldType(Object addable, Field field) {
        String fieldType;
        List<String> fieldParams = new ArrayList<>();
        if (field.getType() == Enum.class) {
            fieldType = "org.hibernate.type.EnumType";
            fieldParams.add("<param name=\"enumClass\">" +
                    beansPackage + "." +
                    SnakeCaseToCamelCase.toCamelCaseCapital(field.getReferredEnum()) +
                    "</param>");
            fieldParams.add("<param name=\"useNamed\">true</param>");
        } else {
            fieldType = field.getType().getTypeName();
            fieldParams.add("<!-- no params -->");
        }

        // Horrible, I know...
        if (addable instanceof TemplateEngine) {
            ((TemplateEngine) addable).addField("fieldColumnType", fieldType)
                    .addField("typeParams", fieldParams);
        } else if (addable instanceof IteratedField) {
            ((IteratedField) addable).addField("fieldColumnType", fieldType)
                    .addField("typeParams", fieldParams);
        }
    }

    private void validateInput() throws ValidationException {
        if (destinationDaoPackage == null) {
            throw new ValidationException("destinationDaoPackage cannot be null.");
        }
        String pattern = "^(?!\\.)[a-z.]*[a-z]$";
        if (!destinationDaoPackage.matches(pattern)) {
            throw new ValidationException(destinationDaoPackage, pattern);
        }
        if (model == null) {
            throw new ValidationException("model cannot be null.");
        }
        if (destinationResourceDir == null) {
            throw new ValidationException("destinationResourceDir cannot be null.");
        }
    }
}
