package com.carlopantaleo.jmodel.utils;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XmlUtilTest {
    private static final String XML_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<root>" +
                    "   <entity>Value</entity>" +
                    "   <another name=\"foo\"/>" +
                    "   <nested>" +
                    "       <bar>val</bar>" +
                    "   </nested>" +
                    "</root>";

    private Document xmlDocument;

    @Before
    public void setup() throws Exception{
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        xmlDocument = builder.parse(IOUtils.toInputStream(XML_TEMPLATE));
        xmlDocument.normalizeDocument();
    }

    @Test
    public void getXmlValue_getsSimpleValue() throws Exception {
        String xmlValue = XmlUtil.getXmlValue(xmlDocument, "root/entity");
        assertEquals("Value", xmlValue);
    }

    @Test
    public void getXmlValue_getsAttributeValue() throws Exception {
        String xmlValue = XmlUtil.getXmlValue(xmlDocument, "root/another/@name");
        assertEquals("foo", xmlValue);
    }

    @Test
    public void getXmlValue_nullIfNotExists() throws Exception {
        String xmlValue = XmlUtil.getXmlValue(xmlDocument, "root/non-existent");
        assertNull(xmlValue);
    }

    @Test
    public void getXmlValue_emptyStringIfNested() throws Exception {
        String xmlValue = XmlUtil.getXmlValue(xmlDocument, "root/nested");
        assertEquals("", xmlValue);
    }

}