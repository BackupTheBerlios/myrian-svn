/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 4:23:10 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

import java.util.List;
import java.util.Iterator;

public class MissingTests extends Element {
    protected MissingTests(XMLResult previous, XMLResult current) {
        super("missing_tests");
        List tests = previous.getChildren("testcase");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            if (!current.hasTest(test.getTestName())) {
                Element missing = new Element("missing");
                missing.setAttribute("name", test.getTestName());
                addContent(missing);
            }
        }
    }

}
