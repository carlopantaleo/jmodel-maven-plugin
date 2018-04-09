package com.carlopantaleo.jmodel.mojos;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class JModelMojoTest {
    @Test
    public void noPrerequisitesMojoRuns() throws Exception {
        TestMojoNoPrerequisites mojo = new TestMojoNoPrerequisites();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration.xml");
        mojo.execute();

        assertTrue(mojo.hasRun());
    }

    @Test
    public void mojoDoesntRunWithDisabledGenerator() throws Exception {
        TestMojoNoPrerequisites mojo = new TestMojoNoPrerequisites();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration-no-javamodel.xml");
        mojo.execute();

        assertFalse(mojo.hasRun());
    }

    @Test
    public void mojoWithPrerequisitesRuns() throws Exception {
        TestMojoWithPrerequisites mojo = new TestMojoWithPrerequisites();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration-mybatis.xml");
        mojo.execute();

        assertTrue(mojo.hasRun());
    }

    @Test(expected = MojoFailureException.class)
    public void mojoWithPrerequisitesComplainsOnIncorrectConfiguration() throws Exception {
        TestMojoWithPrerequisites mojo = new TestMojoWithPrerequisites();
        mojo.setJmodelFileName("jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration-mybatis-incorrect.xml");
        mojo.execute();
    }

    @Test
    public void nonExistentInputFileComplainsGracefully() {
        TestMojoNoPrerequisites mojo = new TestMojoNoPrerequisites();
        mojo.setJmodelFileName("/my/non/existent/path/jmodel.xml");
        mojo.setConfigurationFileName("jmodel-configuration.xml");

        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertTrue(false); // Should never happen
        } catch (MojoFailureException e) {
            assertEquals("FileNotFoundException while loading jModel configuration.", e.getMessage());
        }
    }

    private static class TestMojoNoPrerequisites extends JModelMojo {
        private String configurationFileName = "jmodel-configuration.xml";
        private String jmodelFileName = "jmodel.xml";
        private boolean hasRun = false;

        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
            AtomicReference<Document> jmodelDocument = new AtomicReference<>();
            AtomicReference<Document> jmodelConfigDocument = new AtomicReference<>();
            setupMojo(jmodelConfigDocument, jmodelDocument, configurationFileName, jmodelFileName);

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

        public boolean hasRun() {
            return hasRun;
        }
    }

    private static class TestMojoWithPrerequisites extends JModelMojo {
        private String configurationFileName = "jmodel-configuration.xml";
        private String jmodelFileName = "jmodel.xml";
        private boolean hasRun = false;

        public TestMojoWithPrerequisites() {
            prerequisites("java-generator");
        }

        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
            AtomicReference<Document> jmodelDocument = new AtomicReference<>();
            AtomicReference<Document> jmodelConfigDocument = new AtomicReference<>();
            setupMojo(jmodelConfigDocument, jmodelDocument, configurationFileName, jmodelFileName);

            if (isGeneratorEnabled(jmodelConfigDocument.get(), "mybatis-generator")) {
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

        public boolean hasRun() {
            return hasRun;
        }
    }
}