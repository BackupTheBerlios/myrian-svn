/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 4:23:10 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

public class MissingTests extends Element {
    public static final String NAME = "missing_tests";

    protected MissingTests(XMLResult previous, XMLResult current) {
        super(NAME);
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

    int missingTestCount() {
        List missing =  getChildren("missing");
        final int count = (missing == null) ? 0 : missing.size();
        return count;
    }

}
