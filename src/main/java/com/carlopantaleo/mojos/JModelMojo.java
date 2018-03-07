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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

public abstract class JModelMojo extends AbstractMojo {
    private static final String JMODEL_XSD = "jmodel.xsd";
    private static final String JMODEL_CONFIGURATION_XSD = "jmodel-configuration.xsd";

    protected void loadModelAndConfiguration(String configurationFileName,
                                             String jmodelFileName,
                                             AtomicReference<Document> jmodelConfigDocument,
                                             AtomicReference<Document> jmodelDocument)
            throws MojoFailureException {
        ClassLoader classLoader = getClass().getClassLoader();

        File jmodelConfigFile = new File(classLoader.getResource(configurationFileName).getFile());
        File jmodelConfigSchemaFile = new File(classLoader.getResource(JMODEL_CONFIGURATION_XSD).getFile());
        jmodelConfigDocument.set(getDocument(jmodelConfigFile, jmodelConfigSchemaFile));

        if (!isGeneratorEnabled(jmodelConfigDocument.get())) {
            return;
        }

        File jmodelFile = new File(classLoader.getResource(jmodelFileName).getFile());
        File jmodelSchemaFile = new File(classLoader.getResource(JMODEL_XSD).getFile());
        jmodelDocument.set(getDocument(jmodelFile, jmodelSchemaFile));
    }

    abstract boolean isGeneratorEnabled(Document jmodelConfigDocument) throws MojoFailureException;

    private Document getDocument(File xmlFile, File xsdFile) throws MojoFailureException {
        Document xmlDocument;
        try (FileInputStream xmlIS = new FileInputStream(xmlFile);
             FileInputStream xmlISbis = new FileInputStream(xmlFile);
             FileInputStream xsdIS = new FileInputStream(xsdFile)) {
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
