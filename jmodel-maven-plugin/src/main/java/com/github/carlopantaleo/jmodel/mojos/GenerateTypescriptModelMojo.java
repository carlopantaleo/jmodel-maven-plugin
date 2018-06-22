package com.github.carlopantaleo.jmodel.mojos;

import com.github.carlopantaleo.jmodel.utils.XmlUtil;
import com.github.carlopantaleo.jmodel.generators.TypescriptModelGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This Mojo generates standard Java classes from jmodel.xml.
 */
@Mojo(name = "generate-typescript-model", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Execute(goal = "generate-typescript-model", phase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateTypescriptModelMojo extends GenerateCodeMojo {
    @Parameter(defaultValue = "jmodel.xml")
    private String jmodelFileName = "jmodel.xml";

    @Parameter(defaultValue = "jmodel-configuration.xml")
    private String configurationFileName = "jmodel-configuration.xml";

    @Parameter(defaultValue = "${project.basedir}")
    private String projectDir = System.getProperty("user.dir");

    public void execute() throws MojoExecutionException, MojoFailureException {
        AtomicReference<Document> jmodelDocument = new AtomicReference<>();
        AtomicReference<Document> jmodelConfigDocument = new AtomicReference<>();
        setupMojo(jmodelConfigDocument, jmodelDocument, configurationFileName, jmodelFileName);

        if (!isGeneratorEnabled(jmodelConfigDocument.get(), "typescript-generator")) {
            // Proceed no further, this generator is not enabled.
            return;
        }

        try {
            String destinationDir =
                    XmlUtil.getXmlValue(jmodelConfigDocument.get(),
                            "jmodel-configuration/generators/typescript-generator/destination-dir");

            TypescriptModelGenerator generator =
                    new TypescriptModelGenerator(destinationDir, jmodelDocument.get(), projectDir);
            generator.generateSources();
        } catch (Exception e) {
            throw new MojoExecutionException("Exception while generating sources.", e);
        }
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
