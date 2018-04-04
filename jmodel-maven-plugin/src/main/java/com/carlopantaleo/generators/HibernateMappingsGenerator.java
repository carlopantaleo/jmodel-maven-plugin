package com.carlopantaleo.generators;

import com.carlopantaleo.entities.Enum;
import com.carlopantaleo.entities.Field;
import com.carlopantaleo.entities.Table;
import com.carlopantaleo.exceptions.ValidationException;
import com.carlopantaleo.utils.EntitesExtractor;
import com.carlopantaleo.utils.SnakeCaseToCamelcase;
import com.carlopantaleo.utils.TemplateEngine;
import com.carlopantaleo.utils.TemplateEngine.IteratedField;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class HibernateMappingsGenerator {
    private static final String HBM_TEMPLATE = "hbm-xml-template.xml";

    private final String destinationDaoPackage;
    private final String beansPackage;
    private final Document model;
    private final String projectDir;
    private final String destinationResourceDir;
    private final Log log;

    public HibernateMappingsGenerator(String destinationDaoPackage,
                                      String beansPackage,
                                      String destinationResourceDir,
                                      Document model,
                                      String projectDir,
                                      Log log) {
        this.destinationDaoPackage = destinationDaoPackage;
        this.beansPackage = beansPackage;
        this.model = model;
        this.projectDir = projectDir;
        this.destinationResourceDir = destinationResourceDir;
        this.log = log;
    }

    public void generateSources() throws Exception {
        validateInput();

        EntitesExtractor extractor = new EntitesExtractor(model);

        for (Table table : extractor.extractTablesAndFields()) {
            writeHibernateMappingFile(table);
        }
    }

    private void writeHibernateMappingFile(Table table) throws MojoFailureException, ValidationException {
        URL url = Resources.getResource(HBM_TEMPLATE);
        try {
            String template = Resources.toString(url, Charsets.UTF_8);
            TemplateEngine templateEngine = buildTemplateEngine(template, table);
            writeHbmFile(templateEngine.compile(), table);
        } catch (IOException e) {
            throw new MojoFailureException("Unable to open resource.", e);
        }
    }

    private void writeHbmFile(String result, Table table)
            throws ValidationException, IOException, MojoFailureException {
        String destinationDir = projectDir + "/src/main/resources/" + destinationResourceDir;
        try {
            Files.createDirectories(new File(destinationDir).toPath());
        } catch (Exception e) {
            throw new MojoFailureException("Unable to create directory tree " + destinationDir, e);
        }

        String mappingName = table.getClassName() != null ?
                table.getClassName() : SnakeCaseToCamelcase.toCamelCaseCapital(table.getName());
        String filePath = destinationDir + "/" + mappingName + ".hbm.xml";
        File outFile = new File(filePath);
        Files.write(outFile.toPath(), result.getBytes());
    }

    private TemplateEngine buildTemplateEngine(String template, Table table)
            throws MojoFailureException, ValidationException {
        if (table.getPk().size() != 1) {
            throw new MojoFailureException("Hibernate expects primary keys to be composed only of one field. Table " +
                    table.getName() + " has " + table.getPk().size() + " fields in the primary key.");
        }

        TemplateEngine templateEngine = new TemplateEngine(template)
                .addField("qualified-class-name", beansPackage + "." + table.getClassName())
                .addField("table-name", table.getName());

        Field pk = addAndGetPk(table, templateEngine);
        IteratedField iteratedField = addAndGetIteratedFields(table, pk);

        templateEngine.addIteratedField(iteratedField);
        return templateEngine;
    }

    private IteratedField addAndGetIteratedFields(Table table, Field pk) throws ValidationException {
        IteratedField iteratedField = new IteratedField("field");
        for (Field field : table.getFields()) {
            if (field == pk) {
                continue;
            }

            iteratedField.addField("field-name", field.getName())
                    .addField("field-name", SnakeCaseToCamelcase.toCamelCase(field.getName()))
                    .addField("field-column-name", field.getName());
            addFieldType(iteratedField, field);
            iteratedField.next();
        }
        return iteratedField;
    }

    private Field addAndGetPk(Table table, TemplateEngine templateEngine) throws ValidationException {
        Field pk = Iterables.getFirst(table.getPk(), null);
        templateEngine.addField("pk-field-name", SnakeCaseToCamelcase.toCamelCase(pk.getName()));
        templateEngine.addField("pk-field-column-name", pk.getName());
        addFieldType(templateEngine, pk);
        return pk;
    }

    private void addFieldType(Object addable, Field field) throws ValidationException {
        String fieldType, fieldParams;
        if (field.getType() == Enum.class) {
            fieldType = "org.hibernate.type.EnumType";
            fieldParams = "<param name=\"enumClass\">" +
                    beansPackage + "." +
                    SnakeCaseToCamelcase.toCamelCaseCapital(field.getReferredEnum()) +
                    "</param>";
        } else {
            fieldType = field.getType().getTypeName();
            fieldParams = "<!-- no params -->";
        }

        // Horrible, I know...
        if (addable instanceof TemplateEngine) {
            ((TemplateEngine) addable).addField("field-column-type", fieldType)
                    .addField("type-params", fieldParams);
        } else if (addable instanceof IteratedField) {
            ((IteratedField) addable).addField("field-column-type", fieldType)
                    .addField("type-params", fieldParams);
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
