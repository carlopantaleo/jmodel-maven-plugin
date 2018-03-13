package com.carlopantaleo.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenerateJavaModelMojoTest {
    private final String generatedDir = System.getProperty("user.dir") + "/src/main/java/com/jmodel/generated/";

    @Test
    public void testTableIsGeneratedCorrectly() throws Exception {
        execute();

        Path path = new File(generatedDir + "TestTable.java").toPath();
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        assertEquals(79, lines.size());
        assertEquals("private String primaryKey;", lines.get(6).trim());
        assertEquals("private @Nullable String secondField;", lines.get(7).trim());
        assertEquals("private String thirdField;", lines.get(8).trim());
        assertEquals("private BigDecimal numberField;", lines.get(9).trim());
        assertEquals("private double doubleField;", lines.get(10).trim());
        assertEquals("private float floatField;", lines.get(11).trim());
        assertEquals("private TestEnum enumField;", lines.get(12).trim());
        assertEquals("private boolean booleanField;", lines.get(13).trim());

        assertEquals("public String getPrimaryKey() {", lines.get(15).trim());
        assertEquals("public void setPrimaryKey(String primaryKey) {", lines.get(19).trim());
        assertEquals("public @Nullable String getSecondField() {", lines.get(23).trim());
        assertEquals("public void setSecondField(@Nullable String secondField) {", lines.get(27).trim());
        assertEquals("public String getThirdField() {", lines.get(31).trim());
        assertEquals("public void setThirdField(String thirdField) {", lines.get(35).trim());
        assertEquals("public BigDecimal getNumberField() {", lines.get(39).trim());
        assertEquals("public void setNumberField(BigDecimal numberField) {", lines.get(43).trim());
        assertEquals("public double getDoubleField() {", lines.get(47).trim());
        assertEquals("public void setDoubleField(double doubleField) {", lines.get(51).trim());
        assertEquals("public float getFloatField() {", lines.get(55).trim());
        assertEquals("public void setFloatField(float floatField) {", lines.get(59).trim());
        assertEquals("public TestEnum getEnumField() {", lines.get(63).trim());
        assertEquals("public void setEnumField(TestEnum enumField) {", lines.get(67).trim());
        assertEquals("public boolean getBooleanField() {", lines.get(71).trim());
        assertEquals("public void setBooleanField(boolean booleanField) {", lines.get(75).trim());
    }

    @Test
    public void testEnumIsGeneratedCorrectly() throws Exception {
        execute();

        Path path = new File(generatedDir + "TestEnum.java").toPath();
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));

        assertEquals(6, lines.size());
        assertEquals("public enum TestEnum {", lines.get(2).trim());
        assertEquals("ITEM1,", lines.get(3).trim());
        assertEquals("ITEM2", lines.get(4).trim());
    }

    @Test
    public void nonExistentInputFileComplainsGracefully() {
        GenerateJavaModelMojo generateJavaModelMojo = new GenerateJavaModelMojo();
        generateJavaModelMojo.setJmodelFileName("/my/non/existent/path/jmodel.xml");
        generateJavaModelMojo.setProjectDir(System.getProperty("user.dir"));

        try {
            generateJavaModelMojo.execute();
        } catch (MojoExecutionException e) {
            assertTrue(false); // Should never happen
        } catch (MojoFailureException e) {
            assertEquals("FileNotFoundException while loading jModel configuration.", e.getMessage());
        }
    }

    private void execute() throws MojoExecutionException, MojoFailureException {
        GenerateJavaModelMojo generateJavaModelMojo = new GenerateJavaModelMojo();
        generateJavaModelMojo.setJmodelFileName("jmodel.xml");
        generateJavaModelMojo.setProjectDir(System.getProperty("user.dir"));
        generateJavaModelMojo.execute();
    }
}