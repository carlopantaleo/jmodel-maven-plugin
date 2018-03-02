package com.carlopantaleo.mojos;

import com.carlopantaleo.generators.JavaModelGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

/**
 * This Mojo generates standard Java classes from jmodel.xml.
 */
@Mojo(name = "generate-java-model", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateJavaModelMojo extends AbstractMojo {
    /**
     * Location of the xml model file. Defaults to resource-root/jmodel.xml.
     */
    @Parameter(property = "jmodel.model", defaultValue = "jmodel.xml")
    private String jmodelFileName = "jmodel.xml";

    @Parameter(property = "jmodel.configuration", defaultValue = "jmodel-configuration.xml")
    private String configurationFile;

    @Parameter(property = "jmodel.root-package-dir", defaultValue = "${project.basedir}/src/main/java/")
    private String rootPackageDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        // TODO check if current generator is in configuration

        ClassLoader classLoader = getClass().getClassLoader();
        File jmodelFile = new File(classLoader.getResource(jmodelFileName).getFile());
        File jmodelSchemaFile = new File(classLoader.getResource("jmodel.xsd").getFile());
        Document xmlDocument;
        try (FileInputStream xmlIS = new FileInputStream(jmodelFile);
             FileInputStream xmlISbis = new FileInputStream(jmodelFile);
             FileInputStream xsdIS = new FileInputStream(jmodelSchemaFile)) {
            validateAgainstXSD(xmlIS, xsdIS);

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            xmlDocument = builder.parse(xmlISbis);
            xmlDocument.normalizeDocument();
        } catch (Exception e) {
            throw new MojoFailureException("Exception while trying to validate model file.", e);
        }


        JavaModelGenerator generator =
                new JavaModelGenerator("com.jmodel.generated" /* TODO parametric */, xmlDocument, rootPackageDir);
        try {
            generator.generateSources();
        } catch (Exception e) {
            throw new MojoFailureException("Exception while generating sources.", e);
        }

    }

    private static void validateAgainstXSD(InputStream xml, InputStream xsd) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsd));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
    }

    public String getJmodelFileName() {
        return jmodelFileName;
    }

    public void setJmodelFileName(String jmodelFileName) {
        this.jmodelFileName = jmodelFileName;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public String getRootPackageDir() {
        return rootPackageDir;
    }

    public void setRootPackageDir(String rootPackageDir) {
        this.rootPackageDir = rootPackageDir;
    }
}
