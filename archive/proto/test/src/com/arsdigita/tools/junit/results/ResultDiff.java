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

package com.arsdigita.tools.junit.results;

import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

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
        addDiffElement();
        setAttribute("name", m_current.getSuiteName());
        setAttribute("previous_changelist", m_previous.getChangelist());
        setAttribute("current_changelist", m_current.getChangelist());


        compareCount();
        copyTests();
        compareFailures();
    }

    public String getTestName() {
        return getAttributeValue("name");
    }
    public XMLResult getCurrent() {
        return m_current;
    }

    public XMLResult getPrevious() {
        return m_previous;
    }

    public int missingTestCount() {
        MissingTests missing = (MissingTests) getChild(MissingTests.NAME);
        final int count = (null == missing) ? 0 : missing.missingTestCount();
        return count;
    }

    public int newTestCount() {
        NewTests newtests = (NewTests) getChild(NewTests.NAME);
        final int count = (null == newtests) ? 0 : newtests.newTestCount();
        return count;
    }

    private void addDiffElement() {
        Element diff = new Element("diff");
        addContent(diff);

        Element prev = new Element("previous");
        diff.addContent(prev);

        Element current = new Element("current");
        diff.addContent(current);

        setRunData(prev, m_previous);
        setRunData(current, m_current);
    }

    private void setRunData(Element diffData, XMLResult runInfo) {
        diffData.setAttribute("errors", runInfo.getAttributeValue("errors"));
        diffData.setAttribute("failures", runInfo.getAttributeValue("failures"));
        diffData.setAttribute("tests", Integer.toString(runInfo.getTestCount()));
    }

    private void compareCount() {
        MissingTests missing = new MissingTests(m_previous, m_current);
        if (missing.missingTestCount() > 0) {
            addContent(missing);
        }
        NewTests newTests = new NewTests(m_previous, m_current);
        if (newTests.newTestCount() > 0) {
            addContent(newTests);
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
        m_regressions = new Element("regressions");
        for (Iterator iterator = tests.iterator(); iterator.hasNext();) {
            XMLTestCase test = (XMLTestCase) iterator.next();
            if (!test.passed()) {
                checkForRegressions(test);
            }
        }

        // Don't add regression element unless neccessary.
        List regressions = m_regressions.getChildren();
        if (regressions != null && regressions.size() > 0) {
            addContent(m_regressions);
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
