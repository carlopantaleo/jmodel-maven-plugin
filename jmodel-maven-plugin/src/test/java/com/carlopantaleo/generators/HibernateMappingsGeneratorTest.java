package com.carlopantaleo.generators;

import com.carlopantaleo.mojos.GenerateHibernateMappingsMojo;
import com.carlopantaleo.mojos.GenerateJavaModelMojo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class HibernateMappingsGeneratorTest {
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/resources/generated/";

    @After
    public void clean() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIR));
    }

    @Test
    public void sourcesAreGenerated() throws Exception {
        execute();

        String content = new String(Files.readAllBytes(new File(OUTPUT_DIR + "MyTestTable.hbm.xml").toPath()));
        assertTrue(content.contains(
                String.format("<id name=\"primaryKey\" column=\"PRIMARY_KEY\">%n" +
                "            <type name=\"java.lang.String\">%n" +
                "                <!-- no params -->%n" +
                "            </type>%n" +
                "            <!-- Generator not yet supported -->%n" +
                "        </id>")));
        assertTrue(content.contains(
                String.format("<property name=\"enumField\" column=\"ENUM_FIELD\">%n" +
                "            <type name=\"org.hibernate.type.EnumType\">%n" +
                "                <param name=\"enumClass\">com.jmodel.generated.TestEnum</param>%n" +
                "            </type>%n" +
                "        </property>")));
        assertTrue(content.contains(
                String.format("<property name=\"datetimeField\" column=\"DATETIME_FIELD\">%n" +
                "            <type name=\"java.time.LocalDateTime\">%n" +
                "                <!-- no params -->%n" +
                "            </type>%n" +
                "        </property>")));

    }

    private void execute() throws MojoExecutionException, MojoFailureException {
        GenerateHibernateMappingsMojo generateHibernateMappingsMojo = new GenerateHibernateMappingsMojo();
        generateHibernateMappingsMojo.setJmodelFileName("jmodel.xml");
        generateHibernateMappingsMojo.setProjectDir(System.getProperty("user.dir"));
        generateHibernateMappingsMojo.execute();
    }

}