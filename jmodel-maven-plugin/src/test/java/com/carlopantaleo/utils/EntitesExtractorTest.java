package com.carlopantaleo.utils;

import com.carlopantaleo.entities.Field;
import com.carlopantaleo.entities.Table;
import com.carlopantaleo.entities.Enum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EntitesExtractorTest {
    private Document model = null;

    @Before
    public void getDocument()
            throws IOException, ParserConfigurationException, SAXException {
        Document xmlDocument;
        try (InputStream xmlIS = getClass().getClassLoader().getResourceAsStream("jmodel.xml")) {

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            xmlDocument = builder.parse(xmlIS);
            xmlDocument.normalizeDocument();
        }
        model = xmlDocument;
    }

    @Test
    public void tablesAreExtracted() throws Exception {
        EntitesExtractor extractor = new EntitesExtractor(model);
        List<Table> tables = extractor.extractTablesAndFields();
        assertEquals(2, tables.size());

        Table table = tables.get(0);
        List<Field> fields = table.getFields();
        assertEquals(9, fields.size());
        assertEquals("PRIMARY_KEY", fields.get(0).getName());
        assertEquals(String.class, fields.get(0).getType());
        assertNull(fields.get(0).getDefaultVal());
        assertEquals("DEF", fields.get(2).getDefaultVal());
        assertEquals(BigDecimal.class, fields.get(3).getType());
        assertEquals(Enum.class, fields.get(6).getType());
        assertEquals("TEST_ENUM", fields.get(6).getReferredEnum());
        assertEquals(LocalDateTime.class, fields.get(8).getType());
    }

    @Test
    public void enumsAreExtracted() throws Exception {
        EntitesExtractor extractor = new EntitesExtractor(model);
        List<Enum> enums = extractor.extractEnumsAndItems();
        assertEquals(1, enums.size());

        Enum theEnum = enums.get(0);
        List<String> items = theEnum.getItems();
        assertEquals(2, items.size());
        assertEquals("ITEM1", items.get(0));
    }
}