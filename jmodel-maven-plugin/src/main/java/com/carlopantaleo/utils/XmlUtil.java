package com.carlopantaleo.utils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XmlUtil {
    private static final XPath xPath = XPathFactory.newInstance().newXPath();

    public static @Nullable
    String getXmlValue(Document document, String xpathExpression) throws XPathExpressionException {
        Node parent = (Node) xPath
                .compile(xpathExpression)
                .evaluate(document, XPathConstants.NODE);

        return parent == null ? null : parent.getFirstChild().getNodeValue().trim();
    }
}
