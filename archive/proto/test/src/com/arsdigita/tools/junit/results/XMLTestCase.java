/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 3:56:54 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

public class XMLTestCase extends Element {
    protected XMLTestCase() {
        super("testcase");
    }

    boolean passed() {
        final boolean passed = !(getFailure() == null || getError() == null);
        return passed;
    }

    Element getFailure() {
        Element failure = getChild("failure");
        return failure;
    }

    Element getError() {
        Element error = getChild("error");
        return error;
    }

    String getTestName() {
        String testName = getAttributeValue("name");
        return testName;
    }

}

