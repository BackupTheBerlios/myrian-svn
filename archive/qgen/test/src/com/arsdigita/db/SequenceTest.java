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

package com.arsdigita.db;

import java.math.BigDecimal;
import java.sql.SQLException;
import junit.framework.*;

public class SequenceTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/db/SequenceTest.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public static String sequenceName = "acs_object_id_seq";

    public SequenceTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    protected void setUp() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new SequenceTest("testSequences"));
        return suite;
    }

    public void testSequences() throws SQLException,ClassNotFoundException {
        java.sql.Connection conn = ConnectionManager.getConnection();

        BigDecimal seqValue1 = null;
        BigDecimal seqValue2 = null;

        seqValue1 = Sequences.getNextValue(sequenceName,conn);

        assertNotNull(seqValue1);

        seqValue2 = Sequences.getCurrentValue(sequenceName,conn);

        assertNotNull(seqValue2);
        assertEquals("nextval followed by currval didn't get the " +
                     "same thing.  This might just mean someone else " +
                     "called nextval in the middle.",
                     seqValue1,seqValue2);

        seqValue1 = Sequences.getNextValue(sequenceName,conn);

        assertTrue(! seqValue1.equals(seqValue2));

        ConnectionManager.returnConnection(conn);
    }

}
