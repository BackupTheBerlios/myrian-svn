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

