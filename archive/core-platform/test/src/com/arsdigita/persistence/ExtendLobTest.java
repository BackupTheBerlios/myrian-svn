/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import java.math.*;
import org.apache.log4j.Logger;

/**
 * ExtendLobTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

public class ExtendLobTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/ExtendLobTest.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private static final Logger LOG = Logger.getLogger(ExtendLobTest.class);

    private static final String EXTEND_LOB =
        "com.arsdigita.persistence.ExtendLob";

    public ExtendLobTest(String name) {
        super(name);
    }

    public void test() {
        Session ssn = SessionManager.getSession();

        DataObject data = ssn.create(new OID(EXTEND_LOB, new BigDecimal("0")));
        data.set("lob", "value");
        data.save();
    }

}
