/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

/**
 * A wrapper class that implements some functionality of
 * <code>org.jdom.Element</code> using <code>org.w3c.dom.Element</code>.
 *
 * @author Patrick McNeill (pmcneill@arsdigita.com)
 * @version $Revision: #7 $ $Date: 2002/10/16 $
 * @since ACS 4.5a
 */
public class Element {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/xml/Element.java#7 $" +
        "$Author: richardl $" +
        "$DateTime: 2002/10/16 14:39:19 $";

    private static final Logger s_log = Logger.getLogger
        (Element.class.getName());

    protected org.w3c.dom.Element m_element;
    /* DOM element that is being wrapped */

    /**
     * owner document
     */
    private org.w3c.dom.Document m_doc;

    private static ThreadLocal s_localDocument = new ThreadLocal() {
            public Object initialValue() {
                try {
                    DocumentBuilderFactory builder =
                        DocumentBuilderFactory.newInstance();
                    builder.setNamespaceAware(true);
                    return builder.newDocumentBuilder().newDocument();
                } catch ( ParserConfigurationException e ) {
                    s_log.error(e);
                    throw new UncheckedWrapperException
                        ("INTERNAL: Could not create thread local DOM document.", e);
                }
            }
        };

    private static org.w3c.dom.Document getDocument() {
        return (org.w3c.dom.Document) s_localDocument.get();
    }

    //private static org.w3c.dom.Document s_document;
    //static {
    //    try {
    //        DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
    //        builder.setNamespaceAware(true);
    //        s_document = builder.newDocumentBuilder().newDocument();
    //    } catch ( ParserConfigurationException e ) {
    //        s_log.error(e);
    //    }
    //}

    /*
     * We keep this document internally so that we can create DOM elements
     * appropriately.  When they're assigned to a document, the node will be
     * removed from its owner document and imported into the new document.
     */
    private static org.w3c.dom.Document s_document = null;

    /**
     * Protected constructor to set up factories, etc. Does not actually
     * create a new element.  Used if we are programatically setting the
     * m_element field later.
     */
    protected Element() {
    }

    /**
     * Creates a new element with the given name and no assigned namespace.
     *
     * @param name the name of the element
     */
    public Element( String name ) {
        this();
        m_element = getDocument().createElement(name);
    }

    /**
     * Creates a new element with the given name, and assigns it to the
     * namespace defined at <code>uri</code>.  The namespace prefix is
     * automatically determined.
     *
     * @param name the name of the element
     * @param uri the URI for the namespace definition
     */
    public Element( String name, String uri ) {
        m_element = getDocument().createElementNS( uri, name );
    }

    /**
     * Creates a new element and adds it as a child to this
     * element.  <code>elt.newChildElement("newElt")</code> is
     *  equivalent to
     * <pre>
     * Element newElt = new Element("newElt");
     * elt.addChild(newElt);
     * </pre>
     *
     * @param name the name of the element
     * @return the created child element.
     * @pre m_element != null
     */
    public Element newChildElement(String name) {
        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element result = new Element();
        result.m_element = m_doc.createElement(name);
        this.m_element.appendChild(result.m_element);
        return result;
    }

    /**
     * Creates a new element. Adds it as a child to this element
     * element and assigns it to the namespace defined at <code>uri</code>.
     *  <code>elt.newChildElement("newElt", namespace)</code> is
     *  equivalent to
     * <pre>
     * Element newElt = new Element("newElt", namespace);
     * elt.addChild(newElt);
     * </pre>
     *
     * @param name the name of the Element
     * @param uri the URI for the namespace definition
     * @return the created child element.
     * @pre m_element != null
     */
    public Element newChildElement(String name, String uri) {
        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element result = new Element();
        result.m_element = m_doc.createElementNS(uri, name);
        this.m_element.appendChild(result.m_element);
        return result;
    }

    /**
     * Copies the passed in element and all of its children to a new
     * Element.
     */
    public Element newChildElement(Element copyFrom) {
        if (m_doc == null) {
            m_doc = this.m_element.getOwnerDocument();
        }

        Element copyTo = new Element();
        copyTo.m_element = m_doc.createElementNS
            (copyFrom.m_element.getNamespaceURI(), copyFrom.getName());
        this.m_element.appendChild(copyTo.m_element);

        copyTo.setText(copyFrom.getText());

        NamedNodeMap nnm = copyFrom.m_element.getAttributes();

        if (nnm != null) {
            for (int i = 0; i < nnm.getLength(); i++) {
                Attr attr = (org.w3c.dom.Attr) nnm.item(i);
                copyTo.addAttribute(attr.getName(), attr.getValue());
            }
        }

        Iterator iter = copyFrom.getChildren().iterator();

        while (iter.hasNext()) {
            Element child = (Element) iter.next();
            copyTo.newChildElement(child);
        }

        return copyTo;
    }

    /**
     * Adds an attribute to the element.
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return this element.
     */
    public Element addAttribute( String name, String value ) {
        m_element.setAttribute( name, value );

        return this;
    }

    /**
     * Adds a child element to this element.
     *
     * @param newContent the new child element
     * @return this element.
     */
    public Element addContent( Element newContent ) {
        newContent.importInto(m_element.getOwnerDocument());
        m_element.appendChild(newContent.getInternalElement());

        return this;
    }

    /**
     * Sets the text value of the current element (the part between the
     * tags).  If the passed in text is null then it is converted to
     * the empty string.
     *
     * @param text the text to include
     * @return this element.
     */
    public Element setText( String text ) {
        if (text == null) {
            // This converts the null to the empty string because
            // org.w3c.dom does not like null and HTML does not
            // differentiate between "" and null.  The other option
            // is to throw the NPE which causes other problems
            text = "";
        }
        org.w3c.dom.Text textElem =
            m_element.getOwnerDocument().createTextNode(text);
        m_element.appendChild(textElem);

        return this;
    }

    /**
     * Returns the concatenation of all the text in all child nodes
     * of the current element.
     */
    public String getText() {
        StringBuffer result = new StringBuffer();

        org.w3c.dom.NodeList nl = m_element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);

            if (n.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                result.append(((org.w3c.dom.Text) n).getData());
            }
        }

        return result.toString();
    }

    public Element setCDATASection(String cdata) {
        s_log.debug("Setting CDATA section to '" + cdata + "'.");

        if (cdata == null) {
            cdata = "";
        }

        org.w3c.dom.CDATASection cdataSection =
            m_element.getOwnerDocument().createCDATASection(cdata);

        m_element.appendChild(cdataSection);

        return this;
    }

    public String getCDATASection() {
        StringBuffer result = new StringBuffer();

        org.w3c.dom.NodeList nl = m_element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);

            if (n.getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE) {
                result.append(((org.w3c.dom.CDATASection) n).getData());
            }
        }

        String str = result.toString();

        s_log.debug("Fetched this from CDATA section: " + str);

        return str;
    }

    /**
     * Returns a <code>List</code> of all the child elements nested
     * directly (one level deep) within this element, as <code>Element</code>
     * objects. If this target element has no nested elements, an empty
     * <code>List</code> is returned. The returned list is "live", so
     * changes to it affect the element's actual contents.
     * <p>
     *
     * This performs no recursion, so elements nested two levels deep would
     * have to be obtained with:
     * <pre>
     * Iterator itr = currentElement.getChildren().iterator();
     * while (itr.hasNext()) {
     *    Element oneLevelDeep = (Element)nestedElements.next();
     *    List twoLevelsDeep = oneLevelDeep.getChildren();
     *      // Do something with these children
     *    }
     * </pre>
     * @return list of child <code>Element</code> objects for this element.
     */
    public java.util.List getChildren() {
        java.util.List retval = new java.util.ArrayList();
        org.w3c.dom.NodeList nl = m_element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);
            if (n instanceof org.w3c.dom.Element) {
                Element elt = new Element();
                elt.m_element = (org.w3c.dom.Element)n;
                retval.add(elt);
            }
        }
        return retval;
    }


    public java.util.Map getAttributes() {
        // Retrieve the attributes of the DOM Element
        org.w3c.dom.NamedNodeMap attributeNodeMap =
            m_element.getAttributes();

        // Create the HashMap that we will return the attributes
        // in
        java.util.HashMap returnMap = new java.util.HashMap();

        // Copy the attribute values in the NamedNodeMap to the
        // HashMap
        for (int i = 0; i < attributeNodeMap.getLength(); ++i) {
            // Get the Node
            org.w3c.dom.Node attributeNode = attributeNodeMap.item(i);
            // Copy the name and value to the map
            returnMap.put(attributeNode.getNodeName(),
                          attributeNode.getNodeValue());
        }

        // Return the HashMap
        return returnMap;
    }

    /**
     * Retrieves an attribute value by name.
     * @param name The name of the attribute to retrieve
     * @return The Attr value as a string,
     * or the empty string if that attribute does not have a specified
     * or default value.
     */
    public String getAttribute(String name) {
        return m_element.getAttribute(name);
    }

    public boolean hasAttribute(String name) {
        return m_element.hasAttribute(name);
    }

    public String getName() {
        return m_element.getTagName();
    }


    /**
     * Functions to allow this class to interact appropriately with the
     * Document class (for example, allows nodes to be moved around,
     * and so on).
     *
     * @return the internal DOM Element.
     */
    protected final org.w3c.dom.Element getInternalElement( ) {
        return m_element;
    }

    /**
     * Imports the internal node into another document.
     * This could also be done with a combination of getInternalElement
     * and a setInternalElement function.
     *
     * @param doc the org.w3c.dom.Document to import into
     */
    protected void importInto( org.w3c.dom.Document doc ) {
        /*
          Exception e = new Exception();
          java.io.StringWriter sw = new java.io.StringWriter();
          e.printStackTrace(new java.io.PrintWriter(sw));
          System.out.println(sw.toString().substring(0, 300));
        */
        visitAllAttributes(m_element);
        m_element = (org.w3c.dom.Element)doc.importNode(m_element, true);
    }

    /**
     * Workaround for bug in some versions of Xerces.
     * For some reason, importNode doesn't also copy attribute
     * values unless you call getValue() on them first.  This may
     * be fixed in a later version of Xerces.  In the meantime,
     * calling visitAllAttributes(node) before importNode should
     * help.
     *
     * @param node the org.w3c.dom.Node about to be imported
     */
    public static void visitAllAttributes(org.w3c.dom.Node node) {
        org.w3c.dom.NamedNodeMap nnm = node.getAttributes();
        if (nnm != null) {
            for (int i = 0; i < nnm.getLength(); i++) {
                org.w3c.dom.Attr attr = (org.w3c.dom.Attr)nnm.item(i);
                attr.getValue();
            }
        }
        org.w3c.dom.NodeList nl = node.getChildNodes();
        if (nl != null) {
            for (int i = 0; i < nl.getLength() ; i++) {
                visitAllAttributes(nl.item(i));
            }
        }
    }
}
