/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.pdl.*;
import java.io.StringReader;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class MetadataDebuggingTest extends TestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/MetadataDebuggingTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private static Logger s_log = Logger.getLogger(MetadataDebuggingTest.class);

    public MetadataDebuggingTest(String name) { super(name); }

    private ObjectType m_ot;

    protected void setUp() throws PDLException {
        m_ot = MetadataRoot.getMetadataRoot().getObjectType("debug.Debug");
        if (m_ot == null) {
            PDL p = new PDL();
            String pdl = "model debug;\n"
                + "object type Debug {\n"
                + "    BigInteger[1..1] id = t_debug.id;\n"
                + "    object key(id);\n"
                + "}";
            p.load(new StringReader(pdl), "testfile");
            MetadataRoot root = new MetadataRoot();
            p.generateMetadata(root);
            m_ot = root.getObjectType("debug.Debug");
            if (m_ot == null) { fail("failed to load pdl"); }
        }
    }

    public void testObjectTypeLocation() {
        assertEquals("filename", "testfile", m_ot.getFilename());
        assertEquals("linenumber", 2, m_ot.getLineNumber());
    }

    public void testPropertyLocation() {
        Property p = m_ot.getProperty("id");
        assertEquals("filename", "testfile", p.getFilename());
        assertEquals("linenumber", 3, p.getLineNumber());
    }

}
