package com.github.carlopantaleo.jmodel.mojos;


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
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenerateJavaModelMojoTest {
    private static final String GENERATED_DIR = System.getProperty("user.dir") + "/src/main/java/com/jmodel/generated/";
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/java/com/jmodel";

    @After
    public void clean() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIR));
    }

    @Test
    public void testTableIsGeneratedCorrectly() throws Exception {
        execute();

        Path path = new File(GENERATED_DIR + "MyTestTable.java").toPath();
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));

        {
            int start = 2, i = 0;
            assertEquals(92, lines.size());
            assertEquals("package com.jmodel.generated;", lines.get(start + i++).trim());
            i++;
            assertEquals("import javax.annotation.Nullable;", lines.get(start + i++).trim());
            assertEquals("import java.math.BigDecimal;", lines.get(start + i++).trim());
            assertEquals("import java.time.LocalDateTime;", lines.get(start + i++).trim());
            assertEquals("import java.io.Serializable;", lines.get(start + i++).trim());
            i++;
            assertEquals("public class MyTestTable implements Serializable {", lines.get(start + i++).trim());
            assertEquals("private String primaryKey;", lines.get(start + i++).trim());
            assertEquals("private @Nullable String secondField;", lines.get(start + i++).trim());
            assertEquals("private String thirdField;", lines.get(start + i++).trim());
            assertEquals("private BigDecimal numberField;", lines.get(start + i++).trim());
            assertEquals("private double doubleField;", lines.get(start + i++).trim());
            assertEquals("private float floatField;", lines.get(start + i++).trim());
            assertEquals("private TestEnum enumField;", lines.get(start + i++).trim());
            assertEquals("private boolean booleanField;", lines.get(start + i++).trim());
            assertEquals("private LocalDateTime datetimeField;", lines.get(start + i++).trim());
        }

        {
            int start = 20, i = 0;
            assertEquals("public String getPrimaryKey() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setPrimaryKey(String primaryKey) {", lines.get(start + i++ * 4).trim());
            assertEquals("public @Nullable String getSecondField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setSecondField(@Nullable String secondField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public String getThirdField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setThirdField(String thirdField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public BigDecimal getNumberField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setNumberField(BigDecimal numberField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public double getDoubleField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setDoubleField(double doubleField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public float getFloatField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setFloatField(float floatField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public TestEnum getEnumField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setEnumField(TestEnum enumField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public boolean getBooleanField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setBooleanField(boolean booleanField) {", lines.get(start + i++ * 4).trim());
            assertEquals("public LocalDateTime getDatetimeField() {", lines.get(start + i++ * 4).trim());
            assertEquals("public void setDatetimeField(LocalDateTime datetimeField) {", lines.get(start + i++ * 4).trim());
        }
    }

    @Test
    public void tableWithDefaultClassNameIsCorrect() throws Exception {
        execute();

        Path path = new File(GENERATED_DIR + "AnotherTable.java").toPath();
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));

        assertEquals("public class AnotherTable implements Serializable {", lines.get(6).trim());
    }

    @Test
    public void testEnumIsGeneratedCorrectly() throws Exception {
        execute();

        Path path = new File(GENERATED_DIR + "TestEnum.java").toPath();
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));

        assertEquals(8, lines.size());
        assertEquals("public enum TestEnum {", lines.get(4).trim());
        assertEquals("ITEM1,", lines.get(5).trim());
        assertEquals("ITEM2", lines.get(6).trim());
    }

    private void execute() throws MojoExecutionException, MojoFailureException {
        GenerateJavaModelMojo generateJavaModelMojo = new GenerateJavaModelMojo();
        generateJavaModelMojo.setJmodelFileName("jmodel.xml");
        generateJavaModelMojo.setProjectDir(System.getProperty("user.dir"));
        generateJavaModelMojo.execute();
    }
}