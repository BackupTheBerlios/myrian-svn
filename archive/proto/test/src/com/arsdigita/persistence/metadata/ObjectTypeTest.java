/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.metadata;

import junit.framework.TestCase;
import java.util.*;

/**
 *
 * <p> This class performs unit tests on com.arsdigita.persistence.metadatax.ObjectType </p>
 *
 * @author <a href="mailto:jorris@arsdigita.com">jorris@arsdigita.com</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 *
 * @see com.arsdigita.persistence.metadatax.ObjectType
 */


public class ObjectTypeTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/metadata/ObjectTypeTest.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public ObjectTypeTest(String name) {
        super(name);
    }

    public void testConstruction()  {
        ObjectType parent = new ObjectType("parent");
        ObjectType child = new ObjectType("child", parent);
        assertTrue( child.isSubtypeOf(parent) );
    }

    public void testPropertyAddition()  {
        MetadataRoot root = MetadataRoot.getMetadataRoot();

        ObjectType parent = new ObjectType("parent");
        ObjectType child = new ObjectType("child", parent);
        Property foo = new Property("foo", root.getPrimitiveType("BigInteger"));
        parent.addProperty(foo);

        assertTrue( parent.hasProperty("foo") );
        assertTrue( child.hasProperty("foo") );
        assertEquals( foo, parent.getProperty("foo") );
        assertEquals( foo, child.getProperty("foo") );

        Property bar = new Property("bar", root.getPrimitiveType("BigInteger"));
        Property baz = new Property("baz", root.getPrimitiveType("BigInteger"));

        child.addProperty(bar);
        child.addProperty(baz);

        assertTrue( child.hasProperty("bar") );
        assertEquals( bar, child.getProperty("bar") );
        assertTrue( child.hasProperty("baz") );
        assertEquals( baz, child.getProperty("baz") );

        assertTrue( false == parent.hasProperty("bar") );
        assertTrue( false == parent.hasProperty("baz") );

        Iterator iter = child.getProperties();
        ArrayList list = new ArrayList();
        while(iter.hasNext()) {
            list.add(iter.next());
        }

        assertTrue( list.contains(foo) );
        assertTrue( list.contains(bar) );
        assertTrue( list.contains(baz) );

        try {
            parent.addKeyProperty("nosuchproperty");
            fail("Added a nonexistent key property!");
        } catch (IllegalArgumentException e) {
        }

        parent.addKeyProperty("foo");
        try {
            child.addKeyProperty("bar");
            fail("added a key property to a subtype ObjectType!");
        } catch (IllegalStateException e) {
        }

    }


}
