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

package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.persistence.metadata.*;
import java.math.*;
import java.util.*;
import java.io.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * LinkAttributeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/08/15 $
 */

public class StaticLinkAttributeTest extends LinkAttributeTest {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/StaticLinkAttributeTest.java#5 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    public StaticLinkAttributeTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        load("com/arsdigita/persistence/testpdl/static/LinkAttributes.pdl");
        super.persistenceSetUp();
    }

    String getModelName() {
        return "examples";
    }

}
