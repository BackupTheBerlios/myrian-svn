/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.pdl.*;
import java.io.StringReader;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class MetadataDebuggingTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/MetadataDebuggingTest.java#5 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

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
