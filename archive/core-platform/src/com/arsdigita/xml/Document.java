/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.xml;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Category;

/**
 * A wrapper class that implements some functionality of 
 * <code>org.jdom.Document</code> using <code>org.w3c.dom.Document</code>.
 *
 * @author Patrick McNeill (pmcneill@arsdigita.com)
 * @version ACS 4.5a
 * @since ACS 4.5a
 */
public class Document {
    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/xml/Document.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static Category s_log = 
        Category.getInstance(Document.class.getName());
    
    /**
     * this is the identity XSL stylesheet.  We need to provide the
     * identity transform as XSL explicitly because the default
     * transformer (newTransformer()) strips XML namespace attributes. 
     * Also, this XSLT will strip the <bebop:structure> debugging info
     * from the XML document if present.
     */
    private final static String identityXSL = 
        "<xsl:stylesheet version=\"1.0\""
        + " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
        + "<xsl:output method=\"html\"/>\n" 
        + "<xsl:template match=\"*|@*|text()\">\n"
        + "  <xsl:copy><xsl:apply-templates select=\"node()|@*\"/></xsl:copy>"
        + "\n</xsl:template>\n"
        + "<xsl:template match=\"bebop:structure\" "
        + " xmlns:bebop=\"http://www.arsdigita.com/bebop/1.0\">\n"
        + "</xsl:template>\n"
        + "</xsl:stylesheet>";
    
    /**
     * A single <code>DocumentBuilderFactory</code> to use for 
     * creating Documents.
     */
    protected static DocumentBuilderFactory s_builder = null;

    /**
     * A single <code>DocumentBuilder</code> to use for 
     * creating Documents.
     */
    protected static ThreadLocal s_db = null;

    static {
        s_builder = DocumentBuilderFactory.newInstance();
        s_builder.setNamespaceAware(true);
        s_db = new ThreadLocal() { 
            public Object initialValue() { 
                try {
                    return s_builder.newDocumentBuilder();
                } catch (ParserConfigurationException pce) { 
                    return null;
                }
            }
        };
    }
        
    /* Used to build the DOM Documents that this class wraps */

    /**
     * The internal DOM document being wrapped.
     */
    protected org.w3c.dom.Document m_document;

    /**
     * Creates a new Document class with no root element.
     */
    public Document( ) throws ParserConfigurationException {
        DocumentBuilder db = (DocumentBuilder)s_db.get();
        if (db == null) { 
            throw new ParserConfigurationException
                ("Unable to create a DocumentBuilder");
        }
        m_document = db.newDocument();
    }

    /**
     *
     * Creates a new Document class based on an org.w3c.dom.Document.
     *
     * @param doc the org.w3c.dom.Document
     * 
     */
    public Document( org.w3c.dom.Document doc ) {
        m_document = doc;
    }

    /**
     * Creates a new Document class with the given root element.
     *
     * @param rootNode the element to use as the root node
     */
    public Document( Element rootNode ) throws ParserConfigurationException {
        DocumentBuilder db = (DocumentBuilder)s_db.get();
        if (db == null) { 
            throw new ParserConfigurationException
                ("Unable to create a DocumentBuilder");
        }
        
        m_document = db.newDocument();
        rootNode.importInto(m_document);
        m_document.appendChild(rootNode.getInternalElement());
    }

    public Document( String xmlString ) throws ParserConfigurationException, org.xml.sax.SAXException {
        DocumentBuilder db = (DocumentBuilder)s_db.get();
        if (db == null) { 
            throw new ParserConfigurationException
                ("Unable to create a DocumentBuilder");
        }

        org.w3c.dom.Document domDoc;
        try {
            domDoc = 
                db.parse(new org.xml.sax.InputSource
                    (new java.io.StringReader(xmlString)));
        } catch (java.io.IOException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
        m_document = domDoc;

    }

    /**
     * Sets the root element.
     *
     * @param rootNode the element to use as the root node
     * @return this document.
     */
    public Document setRootElement( Element rootNode ) {
        rootNode.importInto(m_document);
        m_document.appendChild(rootNode.getInternalElement());

        return this;
    }

    /**
     * Creates a new element and sets it as the root.
     * Equivalent to 
     * <pre>
     * Element root = new Element("name", NS); 
     * doc.setRootElement(root);
     * </pre>
     * @param elt the element name 
     * @param ns the element's namespace URI
     * @return The newly created root element.
     */
    public Element createRootElement( String elt, String ns ) {
        org.w3c.dom.Element root = m_document.createElementNS(ns, elt);
        m_document.appendChild(root);
        Element wrapper = new Element();
        wrapper.m_element = root;
        return wrapper;
    }

    /**
     * Creates a new element and sets it as the root.
     * Equivalent to 
     * <pre>
     * Element root = new Element("name", NS); 
     * doc.setRootElement(root);
     * </pre>
     * @param elt the element name 
     * @return The newly created root element.
     */
    public Element createRootElement( String elt ) {
        org.w3c.dom.Element root = m_document.createElement(elt);
        m_document.appendChild(root);
        Element wrapper = new Element();
        wrapper.m_element = root;
        return wrapper;
    }

    /**
     * Returns the root element for the document.  This is the top-level
     * element (the "HTML" element in an HTML document).
     * @return the document's root element.
     */
    public Element getRootElement() { 
        Element root = new Element();
        root.m_element = m_document.getDocumentElement();
        return root;
    }

    

    /**
     * Not a part of <code>org.jdom.Document</code>, this function returns
     * the internal DOM representation of this document.  This method should 
     * only be used when passing the DOM to the translator. It will require 
     * changes once JDOM replaces this class.
     *
     * @return this document.
     */
    public org.w3c.dom.Document getInternalDocument( ) {
        return m_document;
    }

    /** 
     * General toString() method for org.w3c.domDocument.
     *  Not really related to xml.Document, but needed here.
     * Converts an XML in-memory DOM to String representation, using
     * an XSLT identity transformation.
     *
     * @param document the <code>org.w3c.dom.Document</code> object 
     * to convert to a String representation
     * @param indent if <code>true</code>, try to indent elements according to normal
     * XML/SGML indentation conventions (may only work with certain
     * XSLT engines)
     * @return a String representation of <code>document</code>.
     */
    public static String toString(org.w3c.dom.Document document,
                                  boolean indent) {
        Transformer identity;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            StreamSource identitySource = 
                new StreamSource(new StringReader(identityXSL));
            identity = TransformerFactory.newInstance()
                .newTransformer(identitySource);
            if (indent) {
                identity.setOutputProperty("method", "xml");
                identity.setOutputProperty("indent", "yes");
            }
            identity.transform(new DOMSource(document), new StreamResult(os));
        } catch (javax.xml.transform.TransformerException e) {
            s_log.error("error in toString", e);
            return document.toString();
        }
        return os.toString();
    }
    
    /** Convenience wrapper for static toString(Document, boolean),
     *  without additional indenting.
     * @param document the <code>org.w3c.dom.Document</code> to output
     * @return a String representation of <code>document</code>.
     */
    public static String toString(org.w3c.dom.Document document) {
        return toString(document, false);
    }
    
    /** 
     * Generates an XML text representation of this document.
     * @param indent if <code>true</code>, try to indent XML elements according
     * to XML/SGML convention
     * @return a String representation of <code>this</code>.
     */
    public String toString(boolean indent) {
        return toString(m_document, indent);
    }

    /** Generates an XML text representation of this document,
     *  without additional indenting.
     * @return a String representation of <code>this</code>.
     */
    public String toString() {
        return toString(m_document, false);
    }
}
