/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 4:31:28 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

import java.util.List;
import java.util.Iterator;

public class NewTests extends Element {
    protected NewTests(XMLResult previous, XMLResult current) {
        super("new_tests");
        List tests = current.getChildren("testcase");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            if (!previous.hasTest(test.getTestName())) {
                Element missing = new Element("new");
                missing.setAttribute("name", test.getTestName());
                addContent(missing);
            }
        }
    }

}
