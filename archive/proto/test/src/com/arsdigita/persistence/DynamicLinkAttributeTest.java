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
 * @version $Revision: #5 $ $Date: 2003/08/04 $
 */

public class DynamicLinkAttributeTest extends LinkAttributeTest {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/DynamicLinkAttributeTest.java#5 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public DynamicLinkAttributeTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Party.pdl");
        load("com/arsdigita/persistence/testpdl/mdsql/LinkAttributes.pdl");
        super.persistenceSetUp();
    }

    String getModelName() {
        return "mdsql";
    }
}
