package com.carlopantaleo.jmodel.mojos;

import com.carlopantaleo.jmodel.exceptions.ValidationException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertTrue;

public class GenerateCodeMojoTest {
    @Test(expected = MojoFailureException.class)
    public void nullDestinationPackageComplains() throws Exception {
        TestMojo mojo = new TestMojo();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration.xml");
        mojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void emptyDestinationPackageComplains() throws Exception {
        TestMojo mojo = new TestMojo();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration.xml");
        mojo.setDestinationPackage("");
        mojo.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void invalidDestinationPackageComplains() throws Exception {
        TestMojo mojo = new TestMojo();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration.xml");
        mojo.setDestinationPackage(".destination/package");
        mojo.execute();
    }

    @Test
    public void validDestinationPackageRuns() throws Exception {
        TestMojo mojo = new TestMojo();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration.xml");
        mojo.setDestinationPackage("destination.package");
        mojo.execute();

        assertTrue(mojo.hasRun());
    }

    private static class TestMojo extends GenerateCodeMojo {
        private String configurationFileName = "jmodel-configuration.xml";
        private String jmodelFileName = "jmodel.xml";
        private String destinationPackage = null;
        private boolean hasRun = false;

        @Override
        public void execute() throws MojoFailureException {
            AtomicReference<Document> jmodelDocument = new AtomicReference<>();
            AtomicReference<Document> jmodelConfigDocument = new AtomicReference<>();
            setupMojo(jmodelConfigDocument, jmodelDocument, configurationFileName, jmodelFileName);
            try {
                validatePackage(destinationPackage, "destination-package");
            } catch (ValidationException e) {
                throw new MojoFailureException("Invalid package.", e);
            }

            if (isGeneratorEnabled(jmodelConfigDocument.get(), "java-generator")) {
                hasRun = true;
            }
        }

        public String getConfigurationFileName() {
            return configurationFileName;
        }

        public void setConfigurationFileName(String configurationFileName) {
            this.configurationFileName = configurationFileName;
        }

        public String getJmodelFileName() {
            return jmodelFileName;
        }

        public void setJmodelFileName(String jmodelFileName) {
            this.jmodelFileName = jmodelFileName;
        }

        public String getDestinationPackage() {
            return destinationPackage;
        }

        public void setDestinationPackage(String destinationPackage) {
            this.destinationPackage = destinationPackage;
        }

        public boolean hasRun() {
            return hasRun;
        }
    }

}