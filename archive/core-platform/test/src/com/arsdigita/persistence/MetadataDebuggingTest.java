/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.pdl.*;
import java.io.StringReader;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class MetadataDebuggingTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/MetadataDebuggingTest.java#4 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private static Logger s_log = Logger.getLogger(MetadataDebuggingTest.class);

    public MetadataDebuggingTest(String name) { super(name); }

    private ObjectType m_ot;

    protected void setUp() throws PDLException {
        m_ot = MetadataRoot.getMetadataRoot().getObjectType("debug.Debug");
        if (m_ot == null) {
            PDL p = new PDL();
            String pdl = "model debug;\n"
                + "object type Debug {\n"
                + "    BigInteger[1..1] id;\n"
                + "    object key(id);\n"
                + "}";
            p.load(new StringReader(pdl), "testfile");
            p.generateMetadata(MetadataRoot.getMetadataRoot());
            m_ot = MetadataRoot.getMetadataRoot().getObjectType("debug.Debug");
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
