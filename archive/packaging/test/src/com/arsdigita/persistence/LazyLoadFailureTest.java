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
 * LazyLoadFailureTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class LazyLoadFailureTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/LazyLoadFailureTest.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    public LazyLoadFailureTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        super.persistenceSetUp();
    }

    public void test() {
        OID oid = new OID("examples.Node", new BigDecimal(1));
        Session ssn = SessionManager.getSession();
        DataObject node = ssn.create(oid);
        node.set("name", "Test Node");
        node.save();

        DataQuery dq = ssn.retrieveQuery("examples.lazyNodesQuery");
        dq.addEqualsFilter("node.id", new BigDecimal(1));
        if (dq.next()) {
            node = (DataObject) dq.get("node");
        } else {
            fail("Lazy query didn't return any rows.");
        }

        try {
            String name = (String) node.get("lazyProperty");
            fail("Lazy load should have bombed out but didn't. It returned: ("
                 + name + ") instead.");
        } catch (PersistenceException e) {
            // Test passes
        }
    }

}
