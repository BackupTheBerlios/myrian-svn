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

import org.jdom.input.DefaultJDOMFactory;
import org.jdom.Element;

/**
 * ResultJDOMFactory
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class ResultJDOMFactory extends DefaultJDOMFactory {
    public ResultJDOMFactory() {
        super();
    }

    public Element element(String name) {
        if (name.equals("testsuite")) {
            return new XMLResult();
        } else if (name.equals("testcase")) {
            return new XMLTestCase();
        }
        return super.element(name);
    }
}
