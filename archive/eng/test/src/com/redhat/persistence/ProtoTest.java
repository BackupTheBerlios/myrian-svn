/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence;

import junit.framework.TestCase;
//import com.arsdigita.tools.junit.framework.BaseTestCase;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.engine.rdbms.*;
import com.redhat.persistence.pdl.PDL;
import com.arsdigita.db.DbHelper;
import com.arsdigita.runtime.*;
import com.arsdigita.util.jdbc.*;
import java.util.*;
import java.math.*;
import java.sql.*;
import java.io.*;

/**
 * ProtoTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class ProtoTest extends TestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/ProtoTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";


    public ProtoTest(String name) {
        super(name);
    }

    private static final String TEST_PDL =
        "test/pdl/com/arsdigita/persistence/Test.pdl";

    public void test() throws Exception {
        Root root = new Root();
        PDL pdl = new PDL();
        pdl.load(new FileReader(TEST_PDL), TEST_PDL);
        pdl.emit(root);

        SQLWriter w;
        switch (DbHelper.getDatabase()) {
        case DbHelper.DB_ORACLE:
            w = new OracleWriter();
            break;
        case DbHelper.DB_POSTGRES:
            w = new PostgresWriter();
            break;
        default:
            DbHelper.unsupportedDatabaseError("proto test");
            w = null;
            break;
        }

        Session ssn = new Session
            (root, new RDBMSEngine
             (new ConnectionSource() {
                public Connection acquire() {
                    try {
                        Connection conn = Connections.acquire
                            (RuntimeConfig.getConfig().getJDBCURL());
                        conn.setAutoCommit(false);
                        return conn;
                    } catch (SQLException e) {
                        throw new Error(e.getMessage());
                    }
                }
                public void release(Connection conn) {
                    // Do nothing
                }
             }, w), new QuerySource());

        ObjectType TEST = ssn.getRoot().getObjectType("test.Test");
        ObjectType ICLE = ssn.getRoot().getObjectType("test.Icle");
        ObjectType COMPONENT = ssn.getRoot().getObjectType("test.Component");

        Adapter a = new Generic.Adapter();
        ssn.getRoot().addAdapter(Generic.class, a);
	TEST.setJavaClass(Generic.class);
	ICLE.setJavaClass(Generic.class);
	COMPONENT.setJavaClass(Generic.class);

        Generic test = new Generic(TEST, BigInteger.ZERO);
        Property NAME = TEST.getProperty("name");
        Property COLLECTION = TEST.getProperty("collection");
        Property OPT2MANY = TEST.getProperty("opt2many");
        doTest(ssn, test, NAME, COLLECTION);
        doTest(ssn, test, NAME, OPT2MANY);
    }

    private void doTest(Session ssn, Generic obj, Property str, Property col) {
        Property REQUIRED = obj.getType().getProperty("required");
        Generic req = new Generic(REQUIRED.getType(), new BigInteger("10"));
        ssn.create(req);

        ssn.create(obj);
        ssn.set(obj, REQUIRED, req);

        // FIXME: forced this file to compile by commenting out the following line
        //Object obj2 = ssn.retrieve(obj.getType(), obj.getID());
        Object obj2 = null;

//        assertTrue("obj: " + obj + ", obj2: " + obj2, obj == obj2);

        ssn.set(obj, str, "foo");
        assertEquals("foo", ssn.get(obj, str));

        ssn.delete(obj);
        // FIXME: forcing to compile by commenting out
        // assertEquals(null, ssn.retrieve(obj.getType(), obj.getID()));

        ssn.create(obj);
        ssn.set(obj, REQUIRED, req);

        PersistentCollection pc =
            (PersistentCollection) ssn.get(obj, col);

        ObjectType ICLE = ssn.getRoot().getObjectType("test.Icle");
        Object one = new Generic(ICLE, new BigInteger("1"));
        ssn.create(one);
        Object two = new Generic(ICLE, new BigInteger("2"));
        ssn.create(two);
        Object three = new Generic(ICLE, new BigInteger("3"));
        ssn.create(three);

        ssn.add(obj, col, one);
        assertCollection(new Object[] {one}, pc);
        ssn.add(obj, col, two);
        assertCollection(new Object[] {one, two}, pc);
        ssn.add(obj, col, three);
        assertCollection(new Object[] {one, two, three}, pc);
        ssn.remove(obj, col, two);
        assertCollection(new Object[] {one, three}, pc);
        ssn.remove(obj, col, three);
        assertCollection(new Object[] {one}, pc);
        ssn.add(obj, col, three);
        assertCollection(new Object[] {one, three}, pc);

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.flush();

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.delete(obj);
        ssn.flush();
        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.rollback();
    }

    private static void assertCollection(Object[] expected,
                                         PersistentCollection actual) {
        HashSet exp = new HashSet();
        for (int i = 0; i < expected.length; i++) {
            exp.add(expected[i]);
        }

        HashSet act = new HashSet();
        DataSet ds = actual.getDataSet();
        Cursor c = ds.getCursor();
        while (c.next()) {
            act.add(c.get());
        }

        assertEquals(exp, act);
    }

}
