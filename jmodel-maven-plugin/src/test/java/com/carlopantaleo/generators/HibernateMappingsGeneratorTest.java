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

import static org.junit.Assert.*;

public class HibernateMappingsGeneratorTest {
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/resources/generated";

    @After
    public void clean() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIR));
    }

    @Test
    public void sourcesAreGenerated() throws Exception {
        execute();
    }

    private void execute() throws MojoExecutionException, MojoFailureException {
        GenerateHibernateMappingsMojo generateHibernateMappingsMojo = new GenerateHibernateMappingsMojo();
        generateHibernateMappingsMojo.setJmodelFileName("jmodel.xml");
        generateHibernateMappingsMojo.setProjectDir(System.getProperty("user.dir"));
        generateHibernateMappingsMojo.execute();
    }

}