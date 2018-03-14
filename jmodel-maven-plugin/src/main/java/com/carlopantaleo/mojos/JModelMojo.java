package com.carlopantaleo.mojos;

import com.carlopantaleo.utils.XmlUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class JModelMojo extends AbstractMojo {
    private static final String JMODEL_XSD = "jmodel.xsd";
    private static final String JMODEL_CONFIGURATION_XSD = "jmodel-configuration.xsd";

    private List<String> prerequisites = new ArrayList<>();

    protected void setupMojo(AtomicReference<Document> jmodelConfigDocument,
                             AtomicReference<Document> jmodelDocument,
                             String configurationFileName,
                             String jmodelFileName)
            throws MojoFailureException {
        try {
            loadModelAndConfiguration(configurationFileName, jmodelFileName, jmodelConfigDocument, jmodelDocument);
        } catch (FileNotFoundException e) {
            throw new MojoFailureException("FileNotFoundException while loading jModel configuration.", e);
        }
    }

    protected void prerequisites(String... generators) {
        prerequisites = Arrays.asList(generators);
    }

    private void loadModelAndConfiguration(String configurationFileName,
                                           String jmodelFileName,
                                           AtomicReference<Document> jmodelConfigDocument,
                                           AtomicReference<Document> jmodelDocument)
            throws MojoFailureException, FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();

        File jmodelConfigFile = getFile(configurationFileName);
        try (InputStream jmodelConfigSchemaFile = classLoader.getResourceAsStream(JMODEL_CONFIGURATION_XSD)) {
            jmodelConfigDocument.set(getDocument(jmodelConfigFile, jmodelConfigSchemaFile));
        } catch (IOException e) {
            throw new MojoFailureException("IOException while opening config schema file.", e);
        }

        File jmodelFile = getFile(jmodelFileName);
        try (InputStream jmodelSchemaFile = classLoader.getResourceAsStream(JMODEL_XSD)) {
            jmodelDocument.set(getDocument(jmodelFile, jmodelSchemaFile));
        } catch (IOException e) {
            throw new MojoFailureException("IOException while opening model schema file.", e);
        }
    }

    protected boolean isGeneratorEnabled(Document jmodelConfigDocument, String generatorName)
            throws MojoFailureException {
        for (String prerequisite : prerequisites) {
            String got = XmlUtil.getXmlValue(jmodelConfigDocument,
                    "jmodel-configuration/generators/" + prerequisite);
            if (got == null) {
                throw new MojoFailureException(
                        String.format("Generator '%s' must be enabled in order to use the '%s' generator.",
                                prerequisite, generatorName));
            }
        }

        return XmlUtil.getXmlValue(jmodelConfigDocument,
                "jmodel-configuration/generators/" + generatorName) != null;
    }

    /**
     * Tries to get the file passed in {@code fileName} firstly as a resource, then as a regular file.
     *
     * @param fileName the file name.
     * @return
     * @throws FileNotFoundException if the file cannot be found.
     */
    private File getFile(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file;

        URL url = classLoader.getResource(fileName);
        if (url == null) {
            file = new File(fileName);
        } else {
            file = new File(url.getFile());
        }

        if (!file.exists()) {
            throw new FileNotFoundException("File " + fileName + " not found.");
        }

        return file;
    }

    private Document getDocument(File xmlFile, InputStream xsdIS) throws MojoFailureException {
        Document xmlDocument;
        try (FileInputStream xmlIS = new FileInputStream(xmlFile);
             FileInputStream xmlISbis = new FileInputStream(xmlFile)) {
            validateAgainstXSD(xmlIS, xsdIS);

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            xmlDocument = builder.parse(xmlISbis);
            xmlDocument.normalizeDocument();
        } catch (Exception e) {
            throw new MojoFailureException("Exception while trying to validate xml file " + xmlFile, e);
        }
        return xmlDocument;
    }

    private static void validateAgainstXSD(InputStream xml, InputStream xsd) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsd));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
    }
}
