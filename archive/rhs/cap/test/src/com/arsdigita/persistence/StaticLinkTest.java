/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 * StaticLinkTest
 *
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 **/

public class StaticLinkTest extends LinkTest {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/StaticLinkTest.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:48:01 $";

    public StaticLinkTest(String name) {
        super(name);
    }

    String getModel() {
        return "linkTest";
    }
}
