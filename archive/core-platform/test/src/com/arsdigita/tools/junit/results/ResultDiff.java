/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 2:57:56 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;

import org.jdom.Element;

import java.util.List;
import java.util.Iterator;

public final class ResultDiff extends Element {

    public ResultDiff(XMLResult previous, XMLResult current) {
        super("junit_result_diff");
        final boolean namesDiffer = !current.getSuiteName().equals(previous.getSuiteName());
         if (namesDiffer) {
             throw new IllegalArgumentException("Cannot compare test results for different suites! Current: " +
                     current.getSuiteName()+
                     " Previous: " + previous.getSuiteName());
         }

        m_previous = previous;
        m_current = current;

        this.setAttribute("errors", m_current.getAttributeValue("errors"));
        this.setAttribute("failures", m_current.getAttributeValue("failures"));
        this.setAttribute("name", m_current.getSuiteName());
        this.setAttribute("tests", Integer.toString(m_current.getTestCount()));
        m_regressions = new Element("regressions");
        addContent(m_regressions);

        compareCount();
        copyTests();
        compareFailures();
    }

    private void compareCount() {
        final int prevCount = m_previous.getTestCount();
        final int curCount = m_current.getTestCount();
        if (prevCount > curCount) {
            MissingTests missing = new MissingTests(m_previous, m_current);
            this.addContent(missing);
        } else if (prevCount < curCount) {
            NewTests newTests = new NewTests(m_previous, m_current);
            this.addContent(newTests);
        }
    }

    private void copyTests() {
        List tests = m_current.getChildren("testcase");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            XMLTestCase copy = (XMLTestCase) test.clone();
            addContent(copy);
        }

    }
    private void compareFailures() {
        List tests = getChildren("testcase");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            if (!test.passed()) {
                checkForRegressions(test);
            }
        }
    }

    private void checkForRegressions(XMLTestCase test) {
        Element failure = test.getFailure();
        if (null != failure) {
            final String name = test.getTestName();
            if (m_previous.hasTest(name) && m_previous.getTestCase(name).getFailure() == null) {
                setRegression(test, "failure");
            }
            return;
        }

        Element error = test.getError();
        if (null != failure) {
            final String name = test.getTestName();
            if (m_previous.hasTest(name) && m_previous.getTestCase(name).getError() == null) {
                setRegression(test, "failure");
            }
        }
    }

    private void setRegression(XMLTestCase test, String type) {
        test.setAttribute("regression", type);
        Element regression = new Element("regression");
        regression.setAttribute("name", test.getTestName());
        regression.setAttribute("type", type);
        m_regressions.addContent(regression);

    }

    XMLResult m_previous;
    XMLResult m_current;
    Element m_regressions;

}
