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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Set;

public class HibernateMappingsGenerator {
    private final String destinationPackage;
    private final Document model;
    private final String projectDir;
    private final String destinationResourceDir;

    public HibernateMappingsGenerator(String destinationPackage,
                                      String destinationResourceDir,
                                      Document model,
                                      String projectDir) {
        this.destinationPackage = destinationPackage;
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

    private void writeHibernateMappingFile(Table table) {
        // TODO
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
        if (destinationResourceDir == null) {
            throw new ValidationException("destinationResourceDir cannot be null.");
        }
    }
}
