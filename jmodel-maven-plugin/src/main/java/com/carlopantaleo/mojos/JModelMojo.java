package com.carlopantaleo.mojos;

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
import java.util.concurrent.atomic.AtomicReference;

public abstract class JModelMojo extends AbstractMojo {
    private static final String JMODEL_XSD = "jmodel.xsd";
    private static final String JMODEL_CONFIGURATION_XSD = "jmodel-configuration.xsd";

    protected void loadModelAndConfiguration(String configurationFileName,
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

    abstract boolean isGeneratorEnabled(Document jmodelConfigDocument) throws MojoFailureException;

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
