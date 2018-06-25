package com.github.carlopantaleo.jmodel.utils;

import com.github.carlopantaleo.jmodel.entities.Enum;
import com.github.carlopantaleo.jmodel.entities.Field;
import com.github.carlopantaleo.jmodel.entities.Table;
import com.github.carlopantaleo.jmodel.exceptions.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntitesExtractor {
    private final Document model;
    private final XPath xPath = XPathFactory.newInstance().newXPath();

    public EntitesExtractor(Document model) {
        this.model = model;
    }

    public List<Table> extractTablesAndFields() throws XPathExpressionException, ValidationException {
        List<Table> tables = new ArrayList<>();

        extractTables(tables);
        extractFields(tables);
        return tables;
    }

    private void extractTables(List<Table> tables) throws XPathExpressionException {
        NodeList tablesNodes = (NodeList) xPath
                .compile("jmodel/entity")
                .evaluate(model, XPathConstants.NODESET);

        for (int i = 0; i < tablesNodes.getLength(); i++) {
            Table table = new Table();
            table.setName(tablesNodes.item(i)
                    .getAttributes()
                    .getNamedItem("name")
                    .getNodeValue());

            String className =
                    XmlUtil.getXmlValue(model, String.format("jmodel/entity[@name='%s']/@class-name", table.getName()));
            if (className == null) {
                className = SnakeCaseToCamelCase.toCamelCaseCapital(table.getName());
            }
            table.setClassName(className);

            tables.add(table);
        }
    }

    private void extractFields(List<Table> tables) throws XPathExpressionException, ValidationException {
        for (Table table : tables) {
            NodeList fields = (NodeList) xPath
                    .compile(String.format("jmodel/entity[@name='%s']/fields/field", table.getName()))
                    .evaluate(model, XPathConstants.NODESET);

            for (int i = 0; i < fields.getLength(); i++) {
                Field field = makeField(fields.item(i));
                table.addField(field);
                if (field.isPk()) {
                    table.addPkField(field);
                }
            }
        }
    }

    private Field makeField(Node item) throws ValidationException {
        NamedNodeMap attributes = item.getAttributes();
        String name = item.getFirstChild().getNodeValue() != null ?
                item.getFirstChild().getNodeValue().trim() : null;
        String type = attributes.getNamedItem("type") != null ?
                attributes.getNamedItem("type").getNodeValue() : null;
        int length = attributes.getNamedItem("length") != null ?
                Integer.parseInt(attributes.getNamedItem("length").getNodeValue()) : 0;
        boolean pk = attributes.getNamedItem("pk") != null &&
                Boolean.parseBoolean(attributes.getNamedItem("pk").getNodeValue());
        boolean nullable = attributes.getNamedItem("nullable") != null &&
                Boolean.parseBoolean(attributes.getNamedItem("nullable").getNodeValue());
        String defaultVal = attributes.getNamedItem("default") != null ?
                attributes.getNamedItem("default").getNodeValue() : null;
        String referredEnum = attributes.getNamedItem("referred-enum") != null ?
                attributes.getNamedItem("referred-enum").getNodeValue() : null;

        if (type == null) {
            throw new ValidationException("Attribute 'type' for field '" + name + "' is missing.");
        }

        Field field = new Field();
        field.setName(name);
        field.setType(parseFieldType(type));
        if (field.getType() == Enum.class) {
            field.setReferredEnum(referredEnum);
        }
        field.setDefaultVal(defaultVal);
        field.setNullable(nullable);
        field.setLength(length);
        field.setPk(pk);

        return field;
    }

    private Class parseFieldType(String type) {
        switch (type) {
            case "string":
                return String.class;
            case "number":
                return BigDecimal.class;
            case "long":
                return Long.class;
            case "double":
                return Double.class;
            case "float":
                return Float.class;
            case "boolean":
                return Boolean.class;
            case "enum":
                return Enum.class;
            case "datetime":
                return LocalDateTime.class;
            default:
                return null;
        }
    }

    public List<Enum> extractEnumsAndItems() throws XPathExpressionException, ValidationException {
        List<Enum> enums = new ArrayList<>();

        extractEnums(enums);
        extractItems(enums);
        return enums;
    }

    private void extractEnums(List<Enum> enums) throws XPathExpressionException {
        NodeList tablesNodes = (NodeList) xPath
                .compile("jmodel/enum")
                .evaluate(model, XPathConstants.NODESET);

        for (int i = 0; i < tablesNodes.getLength(); i++) {
            Enum theEnum = new Enum();
            theEnum.setName(tablesNodes.item(i)
                    .getAttributes()
                    .getNamedItem("name")
                    .getNodeValue());
            enums.add(theEnum);
        }
    }

    private void extractItems(List<Enum> enums) throws XPathExpressionException, ValidationException {
        for (Enum theEnum : enums) {
            NodeList items = (NodeList) xPath
                    .compile(String.format("jmodel/enum[@name='%s']/item", theEnum.getName()))
                    .evaluate(model, XPathConstants.NODESET);

            for (int i = 0; i < items.getLength(); i++) {
                String item = null;
                if (items.item(i).getFirstChild().getNodeValue() != null) {
                    item = items.item(i).getFirstChild().getNodeValue().trim();
                }
                if (item == null || item.isEmpty()) {
                    throw new ValidationException("Enum " + theEnum.getName() + " cannot have empty items");
                }

                theEnum.addItem(item);
            }
        }
    }
}
