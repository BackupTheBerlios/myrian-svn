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

import java.math.*;

/**
 * DataHandlerTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/04/09 $
 **/

public class DataHandlerTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/DataHandlerTest.java#2 $ by $Author: rhs $, $DateTime: 2003/04/09 16:35:55 $";

    public DataHandlerTest(String name) {
        super(name);
    }

    public void test() {
        Session ssn = SessionManager.getSession();
        DataObject data = ssn.create(new OID("test.dataHandler.Test",
                                             BigInteger.ZERO));
        data.set("value", "foo");
        data.save();

        assertTrue(!FakeDataHandler.DELETED.contains(data));
        data.delete();
        assertTrue(FakeDataHandler.DELETED.contains(data));
    }

}
