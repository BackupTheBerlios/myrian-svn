/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
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
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 */

public class DynamicPartyTest extends PartyTest {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DynamicPartyTest.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
