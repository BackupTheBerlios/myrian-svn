package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;

import com.redhat.persistence.pdl.*;
import java.sql.*;

import junit.framework.*;

/**
 * PersistenceTestSetup
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/09/01 $
 **/

public class PersistenceTestSetup extends BaseTestSetup {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceTestSetup.java#1 $ by $Author: rhs $, $DateTime: 2004/09/01 10:15:50 $";

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
