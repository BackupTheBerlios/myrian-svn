/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence;

/**
 * PartyTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/05/28 $
 */

public class StaticOrderTest extends OrderTest {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/StaticOrderTest.java#2 $ by $Author: rhs $, $DateTime: 2004/05/28 09:10:39 $";

    public StaticOrderTest(String name) {
        super(name);
        m_testType = "static";
    }

    String getModelName() {
        return "examples";
    }

}
