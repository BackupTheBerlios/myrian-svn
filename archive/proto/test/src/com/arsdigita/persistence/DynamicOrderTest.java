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

/**
 * PartyTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/08/04 $
 */

public class DynamicOrderTest extends OrderTest {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/DynamicOrderTest.java#3 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public DynamicOrderTest(String name) {
        super(name);
        m_testType = "dynamic";
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Order.pdl");
        super.persistenceSetUp();
    }

    String getModelName() {
        return "mdsql";
    }

}
