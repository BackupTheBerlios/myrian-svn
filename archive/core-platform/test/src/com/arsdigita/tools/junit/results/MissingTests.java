/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
