/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.db.SQLExceptionHandler;

import junit.framework.*;
import junit.extensions.*;

import java.io.*;
import java.sql.SQLException;
import java.math.BigInteger;

/**
 * This test verifies the exception-type-narrowing functionality.
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 */
public class PersistenceExceptionTest extends PersistenceTestCase {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/PersistenceExceptionTest.java#1 $";

    private Session ssn;

    public PersistenceExceptionTest(String name) {
        super(name);
    }

    // the idea here is to pick an incredibly dirt-simple PDL file that
    // has an insert statement
    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Datatype.pdl");
        super.persistenceSetUp();
    }

    public void setUp() {
        ssn = getSession();
    }

    public void testUniqueConstraintException() {
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.save();
        
        dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        try {
            dt.save();
            fail("Unique constraint violation should have caused error");
        } catch (UniqueConstraintException e) {
            // good
        } catch (Throwable e) {
            // bad
            fail("Unique constraint violation should have caused " + 
                 "UniqueConstraintException, instead caused " + e);
        }
    }

    public void testDbNotAvailableException() {
        // TODO: Figure out other ways to simulate DB failure 
        // than just garbage connection info?  Is this possible?

        java.sql.PreparedStatement insertStmt = null;
        java.sql.Connection badConn = null;
        try {
            badConn = java.sql.DriverManager.getConnection("total garbage");
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // This gets ugly because we resorted to pure JDBC instead
            // of com.arsdigita.db stuff.  So, the SQLException we just caught 
            // is a real unprocessed SQLException.  We process it once, like
            // the com.arsdigita.db code would do.  We then catch it and process
            // it again, like the persistence code would do.  We then see
            // if we got the right result.
            try {
                SQLExceptionHandler.throwSQLException(e);
            } catch (SQLException err) {
                try {
                    throw PersistenceException.newInstance(err);
                } catch (DbNotAvailableException err2) {
                    // good
                } catch (Throwable err2) {
                    fail("Setting garbage connection info should have caused " +
                         "DbNotAvailableException, instead caused " + err2);
                }
            }
        }

        try {
            badConn = java.sql.DriverManager.getConnection("jdbc:totalgarbage");
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // This gets ugly because we resorted to pure JDBC instead
            // of com.arsdigita.db stuff.  So, the SQLException we just caught 
            // is a real unprocessed SQLException.  We process it once, like
            // the com.arsdigita.db code would do.  We then catch it and process
            // it again, like the persistence code would do.  We then see
            // if we got the right result.
            try {
                SQLExceptionHandler.throwSQLException(e);
            } catch (SQLException err) {
                try {
                    throw PersistenceException.newInstance(err);
                } catch (DbNotAvailableException err2) {
                    // good
                } catch (Throwable err2) {
                    fail("Setting garbage connection info should have caused " +
                         "DbNotAvailableException, instead caused " + err2);
                }
            }
        }

        try {
            badConn = java.sql.DriverManager.getConnection("jdbc:oracle:garbage");
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // This gets ugly because we resorted to pure JDBC instead
            // of com.arsdigita.db stuff.  So, the SQLException we just caught 
            // is a real unprocessed SQLException.  We process it once, like
            // the com.arsdigita.db code would do.  We then catch it and process
            // it again, like the persistence code would do.  We then see
            // if we got the right result.
            try {
                SQLExceptionHandler.throwSQLException(e);
            } catch (SQLException err) {
                try {
                    throw PersistenceException.newInstance(err);
                } catch (DbNotAvailableException err2) {
                    // good
                } catch (Throwable err2) {
                    fail("Setting garbage connection info should have caused " +
                         "DbNotAvailableException, instead caused " + err2);
                }
            }
        }

        try {
            badConn = java.sql.DriverManager.getConnection("jdbc:oracle:oci8:@totalgarbage");
            insertStmt =
                badConn.prepareStatement("insert into db_test\n" +
                                      "(theId)\n" +
                                      "values\n" +
                                      "(1)");
            insertStmt.executeUpdate();
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // This gets ugly because we resorted to pure JDBC instead
            // of com.arsdigita.db stuff.  So, the SQLException we just caught 
            // is a real unprocessed SQLException.  We process it once, like
            // the com.arsdigita.db code would do.  We then catch it and process
            // it again, like the persistence code would do.  We then see
            // if we got the right result.
            try {
                SQLExceptionHandler.throwSQLException(e);
            } catch (SQLException err) {
                try {
                    throw PersistenceException.newInstance(err);
                } catch (DbNotAvailableException err2) {
                    // good
                } catch (Throwable err2) {
                    fail("Setting garbage connection info should have caused " +
                         "DbNotAvailableException, instead caused " + err2);
                }
            }
        }
    }
}
