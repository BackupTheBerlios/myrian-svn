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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
public class XMLResult extends Element  {
    protected XMLResult() {
        super("testsuite");
    }


    public String getSuiteName() {
        final String name = getAttributeValue("name");
        return name;
    }

    public int getTestCount() {
        final String count = getAttributeValue("tests");
        return Integer.parseInt(count);
    }

    public int getFailureCount() {
        final String count = getAttributeValue("failures");
        return Integer.parseInt(count);
    }

    public int getErrorCount() {
        final String count = getAttributeValue("errors");
        return Integer.parseInt(count);
    }

    public String getChangelist() {
        final String changelist = getAttributeValue("changelist");
        return changelist;
    }
    public boolean hasTest(String name) {
        return getIndex().containsKey(name);
    }

    public void setChangelist(String changelist) {
        setAttribute("changelist", changelist);
    }

    public XMLTestCase getTestCase(String name) {
        return (XMLTestCase) m_index.get(name);
    }

    private Map getIndex() {
        if (null == m_index) {
            m_index = new HashMap();
            List testcases = getChildren("testcase");
            for (Iterator iterator = testcases.iterator(); iterator.hasNext();) {
                XMLTestCase test = (XMLTestCase) iterator.next();
                final String name = test.getAttributeValue("name");
                m_index.put(name, test);

            }
        }

        return m_index;
    }

    private Map m_index = null;
}

