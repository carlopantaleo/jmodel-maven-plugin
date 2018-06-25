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
import java.util.List;

public class HibernateMappingsGenerator {
    private static final String HBM_TEMPLATE = "hbm/hbm-xml-template.tpl";

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

    private TemplateEngine buildTemplateEngine(String template, Table table) throws ValidationException {
        TemplateEngine templateEngine = new TemplateEngine(template)
                .addField("autogen-warn", SharedConstants.AUTOGEN_WARN)
                .addField("qualified-class-name", beansPackage + "." + table.getClassName())
                .addField("table-name", table.getName());

        List<Field> pk = addAndGetPk(table, templateEngine);
        IteratedField iteratedField = addAndGetIteratedFields(table, pk);

        templateEngine.addIteratedField(iteratedField);
        return templateEngine;
    }

    private IteratedField addAndGetIteratedFields(Table table, List<Field> pk) throws ValidationException {
        IteratedField iteratedField = new IteratedField("field");
        for (Field field : table.getFields()) {
            if (pk.contains(field)) {
                continue;
            }

            addFieldToIteratedField(iteratedField, field);
        }
        return iteratedField;
    }

    private void addFieldToIteratedField(IteratedField iteratedField, Field field)
            throws ValidationException {
        iteratedField.addField("field-name", field.getName())
                .addField("field-name", SnakeCaseToCamelCase.toCamelCase(field.getName()))
                .addField("field-column-name", field.getName());
        addFieldType(iteratedField, field);
        iteratedField.next();
    }

    private List<Field> addAndGetPk(Table table, TemplateEngine templateEngine) throws ValidationException {
        List<Field> pk = table.getPk();

        if (pk.size() == 1) {
            Field id = Iterables.getFirst(pk, null);
            templateEngine.addField("pk-field-name", SnakeCaseToCamelCase.toCamelCase(id.getName()));
            templateEngine.addField("pk-field-column-name", id.getName());
            templateEngine.addField("single-id", true);
            addFieldType(templateEngine, id);
        } else {
            IteratedField iteratedField = new IteratedField("pk-field");
            for (Field field : pk) {
                addFieldToIteratedField(iteratedField, field);
            }

            templateEngine.addField("composite-id", true);
            templateEngine.addIteratedField(iteratedField);
        }

        return pk;
    }

    private void addFieldType(Object addable, Field field) {
        String fieldType, fieldParams;
        if (field.getType() == Enum.class) {
            fieldType = "org.hibernate.type.EnumType";
            fieldParams = "<param name=\"enumClass\">" +
                    beansPackage + "." +
                    SnakeCaseToCamelCase.toCamelCaseCapital(field.getReferredEnum()) +
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
