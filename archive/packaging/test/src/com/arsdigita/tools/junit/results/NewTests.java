/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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
