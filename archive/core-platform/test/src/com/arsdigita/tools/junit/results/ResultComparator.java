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

import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * ResultComparator
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class ResultComparator {
    public static void main(String[] args) throws Exception {
        String canonical = args[0];
        String newFile = args[1];

        SAXBuilder builder = new SAXBuilder();
        builder.setFactory(new ResultJDOMFactory());

        XMLResult previous = (XMLResult) builder.build(canonical).getRootElement();
        XMLResult current = (XMLResult) builder.build(newFile).getRootElement();

        ResultDiff diff = new ResultDiff(previous, current);

        XMLOutputter out = new XMLOutputter("  ", true);
        out.output(diff, System.out);

    }
}
