/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.xml;

import java.io.*;
import junit.framework.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class ACSJDOMTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/xml/ACSJDOMTest.java#6 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public static final String BEBOP_XML_NS =
        "http://www.arsdigita.com/bebop/1.0";

    public ACSJDOMTest ( String name ) {
        super (name);
    }

    public static void main ( String args[] ) {
        junit.textui.TestRunner.run(suite());
    }

    protected void setUp () {
    }

    public static Test suite () {
        TestSuite suite = new TestSuite();
        suite.addTest(new ACSJDOMTest("testJDOMOutput"));
        suite.addTest(new ACSJDOMTest("testJDOMConcurrency"));
        return suite;
    }

    public void testJDOMOutput () throws Exception {
        Transformer xformer =
            TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream os;

        /*
         * Construct a DOM document...
         */
        org.w3c.dom.Document domDoc = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .newDocument();

        org.w3c.dom.Element domPage =
            domDoc.createElementNS(BEBOP_XML_NS, "page");
        domDoc.appendChild(domPage);

        org.w3c.dom.Element domTitle =
            domDoc.createElementNS(BEBOP_XML_NS, "title");
        domPage.appendChild(domTitle);
        domTitle.setAttribute("fontweight", "bold");
        domTitle.appendChild(domDoc.createTextNode("Title goes here"));

        org.w3c.dom.Element domBoxPanel =
            domDoc.createElementNS(BEBOP_XML_NS, "boxPanel");
        domPage.appendChild(domBoxPanel);
        domBoxPanel.setAttribute("bgcolor", "ffffff");

        org.w3c.dom.Element domCell =
            domDoc.createElementNS(BEBOP_XML_NS, "cell");
        domBoxPanel.appendChild(domCell);
        domCell.appendChild(domDoc.createTextNode("Name?"));

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(domDoc), new StreamResult(os));
        String domString = os.toString();

        /*
         * Now make a JDOM document
         */
        Document jdomDoc = new Document();

        Element jdomPage = new Element("page", BEBOP_XML_NS);
        jdomDoc.setRootElement(jdomPage);

        Element jdomTitle = new Element("title", BEBOP_XML_NS);
        jdomPage.addContent(jdomTitle);
        jdomTitle.addAttribute("fontweight", "bold");
        jdomTitle.setText("Title goes here");

        Element jdomBoxPanel = new Element("boxPanel", BEBOP_XML_NS);
        jdomPage.addContent(jdomBoxPanel);
        jdomBoxPanel.addAttribute("bgcolor", "ffffff");

        Element jdomCell = new Element("cell", BEBOP_XML_NS);
        jdomBoxPanel.addContent(jdomCell);
        jdomCell.setText("Name?");

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(jdomDoc.getInternalDocument()),
                           new StreamResult(os));
        String jdomString = os.toString();

        assertEquals("DOMs do not match.\n\n" +
                     "DOM version: " + domString + "\n" +
                     "JDOM version: " + jdomString,
                     domString,
                     jdomString);
    }

    public void testJDOMConcurrency () throws Exception {
        Transformer xformer =
            TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream os;

        Document dom1 = new Document();

        Element dom2root = new Element("rootNode");
        Document dom2 = new Document(dom2root);

        Element dom1root = new Element("rootNode");
        dom1.setRootElement(dom1root);

        Element dom2branch = new Element("branch");
        Element dom1branch = new Element("branch");

        dom1root.addContent(dom1branch);
        dom2root.addContent(dom2branch);

        Element dom2leaf = new Element("leaf");
        Element dom1leaf = new Element("leaf");

        dom2branch.addContent(dom2leaf);
        dom1branch.addContent(dom1leaf);

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(dom1.getInternalDocument()),
                           new StreamResult(os));
        String dom1String = os.toString();

        os = new ByteArrayOutputStream();
        xformer.transform (new DOMSource(dom2.getInternalDocument()),
                           new StreamResult(os));
        String dom2String = os.toString();

        assertEquals("DOMs do not match.\n\n" +
                     "DOM1 version: " + dom1String + "\n" +
                     "DOM2 version: " + dom2String,
                     dom1String,
                     dom2String);
    }

    /*
      assertNotNull(seqValue1);
      assertEquals("nextval followed by currval didn't get the " +
      "same thing.  This might just mean someone else " +
      "called nextval in the middle.",
      seqValue1,seqValue2);

      assert(! seqValue1.equals(seqValue2));
    */


}
