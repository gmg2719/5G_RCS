package com.android.messaging.ui.chatbotservice;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLParserHelper {
    public static String getCDATAFromXmlFile(File xmlFileName, String cdataNode) {
        try (InputStream in =
                     new BufferedInputStream(new FileInputStream(xmlFileName))) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(in);
            NodeList elements = doc.getElementsByTagName(cdataNode);
            for (int i = 0; i < elements.getLength(); i++) {
                Node e = elements.item(i);
//                System.out.println(e.getTextContent());
                return e.getTextContent();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
