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

/**
 * PartyTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 */

public class StaticOrderTest extends OrderTest {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/StaticOrderTest.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public StaticOrderTest(String name) {
        super(name);
        m_testType = "static";
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceSetUp();
    }

    String getModelName() {
        return "examples";
    }

}
