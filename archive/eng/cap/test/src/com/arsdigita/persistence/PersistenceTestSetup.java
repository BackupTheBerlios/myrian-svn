/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;

import com.redhat.persistence.pdl.*;
import java.sql.*;

import junit.framework.*;

/**
 * PersistenceTestSetup
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/09/01 $
 **/

public class PersistenceTestSetup extends BaseTestSetup {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceTestSetup.java#2 $ by $Author: dennis $, $DateTime: 2004/09/01 11:40:07 $";

    public PersistenceTestSetup(TestSuite suite) {
        super(suite);
    }

    protected void setUp() throws Exception {
        super.setUp();
        // XXX: hack for getting session to load via static
        // initializer in PersistenceTestCase
        Class dummy = PersistenceTestCase.class;
        Session ssn = SessionManager.getSession();
        Connection conn = ssn.getConnection();
        Schema.load(ssn.getMetadataRoot().getRoot(), conn);
        conn.commit();
    }

    protected void tearDown() throws Exception {
        Session ssn = SessionManager.getSession();
        Connection conn = ssn.getConnection();
        Schema.unload(ssn.getMetadataRoot().getRoot(), conn);
        super.tearDown();
        conn.commit();
    }

}
