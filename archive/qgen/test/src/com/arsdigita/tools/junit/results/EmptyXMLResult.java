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

import com.arsdigita.util.Assert;

/**
 *  EmptyXMLResult
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #1 $ $Date Nov 6, 2002 $
 */
public class EmptyXMLResult extends XMLResult {
    public EmptyXMLResult(String filename) {
        super();

        String testName = filename.substring(filename.indexOf("-") + 1, filename.indexOf(".xml"));
        setAttribute("name", testName);
        setAttribute("tests", "0");
        setAttribute("failures", "0");
        setAttribute("errors", "0");
    }
}
