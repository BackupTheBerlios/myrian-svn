/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 4:31:28 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

public class NewTests extends Element {
    public static final String NAME = "new_tests";
    protected NewTests(XMLResult previous, XMLResult current) {
        super(NAME);
        List tests = current.getChildren("testcase");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            if (!previous.hasTest(test.getTestName())) {
                Element newtest = new Element("new");
                newtest.setAttribute("name", test.getTestName());
                addContent(newtest);
            }
        }
    }

    int newTestCount() {
        List newtests =  getChildren("new");
        final int count = (newtests == null) ? 0 : newtests.size();
        return count;
    }


}
