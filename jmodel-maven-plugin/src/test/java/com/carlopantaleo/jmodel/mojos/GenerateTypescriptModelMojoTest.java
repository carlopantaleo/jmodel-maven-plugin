package com.carlopantaleo.jmodel.mojos;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class GenerateTypescriptModelMojoTest {
    private static final String GENERATED_DIR = System.getProperty("user.dir") + "/src/main/typescript/generated/";
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/typescript/";

    @After
    public void clean() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIR));
    }

    @Test
    public void works() throws Exception {
        execute();

        {
            String expectedTableOutput = "import {TestEnum} from 'test-enum.ts';\n" +
                    "\n" +
                    "export class MyTestTable {\n" +
                    "    primaryKey: string;\n" +
                    "    secondField: string;\n" +
                    "    thirdField: string;\n" +
                    "    numberField: number;\n" +
                    "    doubleField: number;\n" +
                    "    floatField: number;\n" +
                    "    enumField: TestEnum;\n" +
                    "    booleanField: boolean;\n" +
                    "    datetimeField: Date;\n" +
                    "}";

            Path path = new File(GENERATED_DIR + "my-test-table.ts").toPath();
            String content = new String(Files.readAllBytes(path), Charset.forName("UTF8"));
            assertEquals(expectedTableOutput, content);
        }

        {
            String expectedEnumOutput = "export enum TestEnum {\n" +
                    "    ITEM1,\n" +
                    "    ITEM2\n" +
                    "}";

            Path path = new File(GENERATED_DIR + "test-enum.ts").toPath();
            String content = new String(Files.readAllBytes(path), Charset.forName("UTF8"));
            assertEquals(expectedEnumOutput, content);
        }
    }


    private void execute() throws MojoExecutionException, MojoFailureException {
        GenerateTypescriptModelMojo generateTypescriptModelMojo = new GenerateTypescriptModelMojo();
        generateTypescriptModelMojo.setJmodelFileName("jmodel.xml");
        generateTypescriptModelMojo.setProjectDir(System.getProperty("user.dir"));
        generateTypescriptModelMojo.execute();
    }

}