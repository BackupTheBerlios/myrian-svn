/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 * StaticLinkTest
 *
 * @version $Revision: #2 $ $Date: 2004/03/30 $
 **/

public class StaticLinkTest extends LinkTest {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/StaticLinkTest.java#2 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public StaticLinkTest(String name) {
        super(name);
    }

    String getModel() {
        return "linkTest";
    }
}
