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
 * @version $Revision: #6 $ $Date: 2004/04/07 $
 */

public class DynamicPartyTest extends PartyTest {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DynamicPartyTest.java#6 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public DynamicPartyTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Party.pdl");
        super.persistenceSetUp();
    }

    String getModelName() {
        return "mdsql";
    }

}
