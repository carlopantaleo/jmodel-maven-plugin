package com.carlopantaleo.mojos;

import com.carlopantaleo.exceptions.ValidationException;
import com.carlopantaleo.generators.JavaModelGenerator;
import com.carlopantaleo.utils.XmlUtil;
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
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Mojo generates standard Java classes from jmodel.xml.
 */
@Mojo(name = "generate-java-model", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateJavaModelMojo extends JModelMojo {
    @Parameter(property = "jmodel.model", defaultValue = "jmodel.xml")
    private String jmodelFileName = "jmodel.xml";

    @Parameter(property = "jmodel.configuration", defaultValue = "jmodel-configuration.xml")
    private String configurationFile = "jmodel-configuration.xml";

    @Parameter(property = "jmodel.root-package-dir", defaultValue = "${project.basedir}/src/main/java/")
    private String rootPackageDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        AtomicReference<Document> jmodelDocument = new AtomicReference<>();
        AtomicReference<Document> jmodelConfig = new AtomicReference<>();

        // TODO check if current generator is in configuration
        loadModelAndConfiguration(configurationFile, jmodelFileName, jmodelConfig, jmodelDocument);

        try {
            String destinationPackage =
                XmlUtil.getXmlValue(jmodelConfig.get(),
                        "jmodel-configuration/generators/java-generator/destination-package");
        validateDestinationPackage(destinationPackage);


        JavaModelGenerator generator =
                new JavaModelGenerator(destinationPackage, jmodelDocument.get(), rootPackageDir);
            generator.generateSources();
        } catch (Exception e) {
            throw new MojoExecutionException("Exception while generating sources.", e);
        }

    }

    private void validateDestinationPackage(String destinationPackage) throws ValidationException {
        if (destinationPackage == null) {
            throw new ValidationException("'destination-package' is mandatory.");
        }
        String pattern = "^(?!\\.)[a-z\\.]*[a-z]$";
        if (!destinationPackage.matches(pattern)) {
            throw new ValidationException("destination-package", pattern);
        }
    }

    @Override
    boolean isGeneratorEnabled(Document jmodelConfigDocument) throws MojoFailureException {
        return XmlUtil.getXmlValue(jmodelConfigDocument,
                "jmodel-configuration/generators/java-generator") != null;
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
