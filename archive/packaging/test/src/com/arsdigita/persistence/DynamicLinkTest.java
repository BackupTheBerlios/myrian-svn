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
 * DynamicLinkTest
 *
 * @version $Revision: #1 $ $Date: 2003/09/09 $
 **/

public class DynamicLinkTest extends LinkTest {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/DynamicLinkTest.java#1 $ by $Author: rhs $, $DateTime: 2003/09/09 17:32:16 $";

    public DynamicLinkTest(String name) {
        super(name);
    }

    String getModel() {
        return "mdsql.linkTest";
    }
}
