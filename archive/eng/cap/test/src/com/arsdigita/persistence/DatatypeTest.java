/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence;

import junit.framework.*;
import com.arsdigita.persistence.pdl.*;
import java.sql.*;
import java.math.*;
import java.util.*;
import java.io.*;

/**
 * DatatypeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public class DatatypeTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/DatatypeTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Session ssn;

    //    private final static int LOB_SIZE = 1000000;
    private final static int LOB_SIZE = 1000000;
    private static final Long LONG_VALUE = new Long(100L);
    private static final java.util.Date DATE_VALUE = new java.util.Date(0);

    public DatatypeTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        super.persistenceTearDown();
    }

    public void setUp() {
        ssn = getSession();
    }

    public void test() throws Exception {
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.set("bigInteger", BigInteger.ONE);
        dt.set("bigDecimal", new BigDecimal(0));
        dt.set("boolean", Boolean.TRUE);
        dt.set("byte", new Byte((byte)42));
        dt.set("character", new Character('c'));
        dt.set("date", DATE_VALUE);
        dt.set("double", new Double(75));
        dt.set("float", new Float(3.14159));
        dt.set("integer", new Integer(100));
        dt.set("long", LONG_VALUE);
        dt.set("short", new Short((short)30));
        dt.set("string", "This is a string.");
	/*
        byte[] bytes = new byte[LOB_SIZE];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        dt.set("blob", bytes);

        StringBuffer charBuf = new StringBuffer(LOB_SIZE);
        for (int i = 0; i < LOB_SIZE; i++) {
            charBuf.append('a' + (i % 26));
        }
        String chars = charBuf.toString();
        dt.set("clob", chars);
	*/
        dt.save();

        dt = ssn.retrieve(new OID("examples.Datatype", BigInteger.ZERO));
	/*
        byte[] fetchedBytes = (byte[]) dt.get("blob");
        assert("Blob not retrieved correctly.",
               Arrays.equals(bytes, fetchedBytes));
        assertEquals("Clob was not retrieved correctly.",
                     chars,
                     dt.get("clob"));
	*/

    }

    public void testDate() {
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.set("date", new java.util.Date(1000));
        dt.save();

        dt = ssn.retrieve(new OID("examples.Datatype", BigInteger.ZERO));
        java.util.Date d = (java.util.Date) dt.get("date");
        assertEquals("Date was not saved and retrieved properly.",
                     new java.util.Date(1000),
                     d);
    }

    public void testQuery() {
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.set("bigInteger", BigInteger.ONE);
        dt.set("bigDecimal", new BigDecimal(0));
        dt.set("boolean", Boolean.TRUE);
        dt.set("byte", new Byte((byte)42));
        dt.set("character", new Character('c'));
        dt.set("date", DATE_VALUE);
        dt.set("double", new Double(75));
        dt.set("float", new Float(3.14159));
        dt.set("integer", new Integer(100));
        dt.set("long", LONG_VALUE);
        dt.set("short", new Short((short)30));
        dt.set("string", "This is a string.");
        dt.save();
        DataQuery dq = ssn.retrieveQuery("examples.TypedQuery");
        while (dq.next()) {
            assertEquals("incorrect 'id'",
                         BigInteger.ZERO,
                         dq.get("id"));
            assertEquals("incorrect 'bigInteger'",
                         BigInteger.ONE,
                         dq.get("bigInteger"));
            assertEquals("incorrect 'bigDecimal'",
                         new BigDecimal(0),
                         dq.get("bigDecimal"));
            assertEquals("incorrect 'boolean'",
                         Boolean.TRUE,
                         dq.get("boolean"));
            assertEquals("incorrect 'byte'",
                         new Byte((byte)42),
                         dq.get("byte"));
            assertEquals("incorrect 'character'",
                         new Character('c'),
                         dq.get("character"));
            assertEquals("incorrect 'date'",
                         new java.util.Date(0),
                         dq.get("date"));
            assertEquals("incorrect 'double'",
                         new Double(75),
                         dq.get("double"));
            assertEquals("incorrect 'float'",
                         new Float(3.14159),
                         dq.get("float"));
            assertEquals("incorrect 'integer'",
                         new Integer(100),
                         dq.get("integer"));
            assertEquals("incorrect 'long'",
                         LONG_VALUE,
                         dq.get("long"));
            assertEquals("incorrect 'short'",
                         new Short((short)30),
                         dq.get("short"));
            assertEquals("incorrect 'string'",
                         "This is a string.",
                         dq.get("string"));
        }
        dq.close();

    }
}
