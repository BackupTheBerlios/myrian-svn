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

package com.arsdigita.persistence.pdl;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;
import com.redhat.persistence.pdl.PDL;

import java.io.*;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * AssociationMetadataTest
 *
 * @author <a href="mailto:ashah@redhat.com">ashah@redhat.com</a>
 * @version $Revision: #2 $ $Date: 2003/09/10 $
 **/

public class AssociationMetadataTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/pdl/AssociationMetadataTest.java#2 $ by $Author: rhs $, $DateTime: 2003/09/10 10:46:29 $";

    private static Logger s_log =
        Logger.getLogger(AssociationMetadataTest.class);

    public AssociationMetadataTest(String name) {
        super(name);
    }

    private Root m_root;
    private ObjectType m_ot1;
    private ObjectType m_ot2;
    private ObjectType m_ot3;

    private static final String FILE =
        "test/pdl/com/arsdigita/persistence/Association.pdl";

    protected void setUp() throws Exception {
        super.setUp();
        m_root = new Root();
        PDL pdl = new PDL();
        pdl.load(new FileReader(FILE), FILE);
        pdl.emit(m_root);
        m_ot1 = m_root.getObjectType("Association.Obj1");
        m_ot2 = m_root.getObjectType("Association.Obj2");
        m_ot3 = m_root.getObjectType("Association.Obj3");
    }

    public void testLinkAttribute1() {
        Link l = (Link) m_ot1.getProperty("obj2");
        assertEquals(m_ot2, l.getTo().getType());

        verifyLink(l);

        assertFalse(l.isCollection());
        assertFalse(l.isNullable());
        assertFalse(l.isComponent());
    }

    public void testLinkAttribute2() {
        Link l = (Link) m_ot1.getProperty("obj3");
        assertEquals(m_ot3, l.getTo().getType());

        verifyLink(l);

        assertTrue(l.isCollection());
        assertTrue(l.isNullable());
        assertTrue(l.isComponent());
    }

    private void verifyLink(Link l) {
        // collection and nullability should match
        assertEquals
            (l.isCollection(), l.getFrom().getReverse().isCollection());
        assertEquals(l.isNullable(), l.getFrom().getReverse().isNullable());

        // both reverses should be components
        assertTrue(l.getTo().getReverse().isComponent());
        assertTrue(l.getFrom().getReverse().isComponent());

        // both to and from should be nonnullable, noncollections
        assertFalse(l.getTo().isNullable());
        assertFalse(l.getTo().isCollection());
        assertFalse(l.getFrom().isCollection());
        assertFalse(l.getFrom().isNullable());

        // component should propagate
        assertEquals(l.isComponent(), l.getTo().isComponent());
    }

    public void testComposite() {
        Role r = (Role) m_ot1.getProperty("test");
        assertTrue(r.isComponent());
    }
}
