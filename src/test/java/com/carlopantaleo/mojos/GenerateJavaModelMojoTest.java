package com.carlopantaleo.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenerateJavaModelMojoTest {
    private final String generatedDir = System.getProperty("user.dir") + "/src/main/java/com/jmodel/generated/";

    @Test
    public void works() throws Exception {
        execute();

        Path path = new File(generatedDir + "TestTable.java").toPath();
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        assertEquals(79, lines.size());
    }

    private void execute() throws MojoExecutionException, MojoFailureException {
        GenerateJavaModelMojo generateJavaModelMojo = new GenerateJavaModelMojo();
        generateJavaModelMojo.setJmodelFileName("jmodel.xml");
        generateJavaModelMojo.setRootPackageDir(System.getProperty("user.dir"));
        generateJavaModelMojo.execute();
    }
}