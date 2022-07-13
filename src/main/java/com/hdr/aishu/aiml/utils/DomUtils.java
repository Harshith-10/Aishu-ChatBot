// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml.utils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.Node;

public class DomUtils
{
    public static Node parseFile(final String fileName) throws Exception {
        final File file = new File(fileName);
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        final Node root = doc.getDocumentElement();
        return root;
    }
    
    public static Node parseString(final String string) throws Exception {
        final InputStream is = new ByteArrayInputStream(string.getBytes("UTF-16"));
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();
        final Node root = doc.getDocumentElement();
        return root;
    }
    
    public static String nodeToString(final Node node) {
        final StringWriter sw = new StringWriter();
        try {
            final Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("omit-xml-declaration", "yes");
            t.setOutputProperty("indent", "no");
            t.transform(new DOMSource(node), new StreamResult(sw));
        }
        catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }
}
