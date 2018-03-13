package com.carlopantaleo.mojos;

import com.carlopantaleo.exceptions.ValidationException;
import com.carlopantaleo.generators.JavaModelGenerator;
import com.carlopantaleo.utils.XmlUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;

import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Mojo generates standard Java classes from jmodel.xml.
 */
@Mojo(name = "generate-java-model", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Execute(goal = "generate-java-model", phase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateJavaModelMojo extends JModelMojo {
    @Parameter(defaultValue = "jmodel.xml")
    private String jmodelFileName = "jmodel.xml";

    @Parameter(defaultValue = "jmodel-configuration.xml")
    private String configurationFileName = "jmodel-configuration.xml";

    @Parameter(defaultValue = "${project.basedir}")
    private String projectDir = System.getProperty("user.dir");

    public void execute() throws MojoExecutionException, MojoFailureException {
        AtomicReference<Document> jmodelDocument = new AtomicReference<>();
        AtomicReference<Document> jmodelConfigDocument = new AtomicReference<>();

        try {
            loadModelAndConfiguration(configurationFileName, jmodelFileName, jmodelConfigDocument, jmodelDocument);
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("FileNotFoundException while loading jModel configuration.", e);
        }

        if (!isGeneratorEnabled(jmodelConfigDocument.get())) {
            // Proceed no further, this generator is not enabled.
            return;
        }

        try {
            String destinationPackage =
                XmlUtil.getXmlValue(jmodelConfigDocument.get(),
                        "jmodel-configuration/generators/java-generator/destination-package");
        validateDestinationPackage(destinationPackage);

        JavaModelGenerator generator =
                new JavaModelGenerator(destinationPackage, jmodelDocument.get(), projectDir);
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

    public String getConfigurationFileName() {
        return configurationFileName;
    }

    public void setConfigurationFileName(String configurationFileName) {
        this.configurationFileName = configurationFileName;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }
}
