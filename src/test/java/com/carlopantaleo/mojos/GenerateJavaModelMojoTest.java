package com.carlopantaleo.mojos;


import org.junit.Test;

public class GenerateJavaModelMojoTest {
    @Test
    public void works() throws Exception {
        GenerateJavaModelMojo generateJavaModelMojo = new GenerateJavaModelMojo();
        generateJavaModelMojo.setJmodelFileName("jmodel.xml");
        generateJavaModelMojo.setRootPackageDir(System.getProperty("user.dir"));
        generateJavaModelMojo.execute();
    }
}