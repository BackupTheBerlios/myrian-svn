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

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * MetadataRootTest    (Copyright 2001 ArsDigita Corporation)
 *
 * <p> This class performs unit tests on com.arsdigita.persistence.metadatax.MetadataRoot </p>
 *
 * @author <a href="mailto:jorris@arsdigita.com">jorris@arsdigita.com</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 *
 * @see com.arsdigita.persistence.metadata.MetadataRoot
 */

public class MetadataRootTest extends TestCase
{

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/metadata/MetadataRootTest.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    public MetadataRootTest(String name) {
        super(name);
    }

    public void testModels() {
        MetadataRoot root = getNewRoot();
        Model foo = new Model("foo");
        root.addModel(foo);
        assertTrue( root.hasModel("foo") );
        assertEquals(foo, root.getModel("foo"));

        Model bar = new Model("bar");
        root.addModel(bar);
        Model category = new Model("com.arsdigita.categorization");
        root.addModel(category);

        Iterator iter = root.getModels();
        ArrayList list = new ArrayList();
        while(iter.hasNext())  {
            list.add(iter.next());
        }

        assertTrue( list.contains(foo) );
        assertTrue( list.contains(bar) );
        assertTrue( list.contains(category) );

    }

    public void testRemoveModelsViaIterator() {
        MetadataRoot root = getNewRoot();
        root.addModel(new Model("smurf"));
        Iterator iter = root.getModels();

        // Just to be evil
        try {
            iter = root.getModels();
            while(iter.hasNext())  {
                iter.next();
                iter.remove();
                fail("Successfully removed models from MetadataRoot via iterator!");
            }

        } catch (UnsupportedOperationException e) {
        }

    }

    public void testDuplicateModels() {
        MetadataRoot root =  getNewRoot();
        Model m = new Model("grick");
        root.addModel(m);

        try {
            root.addModel(m);
            fail("added the same model twice to metadata root!");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetObjectType()  {
        ObjectType gronk = new ObjectType("gronk");
        Model m = new Model("com.arsdigita.stuff");
        m.addDataType(gronk);
        assertNotNull(m.getObjectType("gronk"));
        MetadataRoot root = getNewRoot();
        root.addModel(m);

        String name = "com.arsdigita.stuff.gronk";
        ObjectType found = root.getObjectType(name);
        assertNotNull(found);

        // mispelling of model name
        found = root.getObjectType("com.ardigita.stuff.gronk");
        assertTrue( null == found );

        // missing model
        found = root.getObjectType(".gronk");
        assertTrue( null == found );

        // No such object type
        assertTrue( null == root.getObjectType("com.arsdigita.stuff.foo"));


    }

    public void testGetQueryType()  {
        Model m = new Model("com.arsdigita.stuff");
        QueryType query = new QueryType("QueryOfHappiness", new Event());
        m.addDataType( query );
        MetadataRoot root = getNewRoot();
        root.addModel(m);
        assertEquals( query, root.getQueryType("com.arsdigita.stuff.QueryOfHappiness"));
        assertEquals( null, root.getQueryType("no.such.model.QueryOfHappiness"));
        assertEquals( null, root.getQueryType("com.arsdigita.stuff.NoSuchQuery"));
    }

    public void testGetDataOperationType()  {
        Model m = new Model("com.arsdigita.stuff");

        DataOperationType op = new DataOperationType("Operation", new Event());
        m.addDataOperationType( op );
        MetadataRoot root = getNewRoot();
        root.addModel(m);
        assertEquals( op, root.getDataOperationType("com.arsdigita.stuff.Operation"));
        assertEquals( null, root.getDataOperationType("no.such.model.Operation"));
        assertEquals( null, root.getDataOperationType("com.arsdigita.stuff.NoSuchQuery"));
    }


    // Simple regression test
    public void testGetPrimitiveType() {
        MetadataRoot root = getNewRoot();

        assertEquals( MetadataRoot.BIGINTEGER, root.getPrimitiveType("BigInteger"));
        assertEquals( MetadataRoot.BIGDECIMAL, root.getPrimitiveType("BigDecimal"));
        assertEquals( MetadataRoot.BOOLEAN, root.getPrimitiveType("Boolean"));
        assertEquals( MetadataRoot.BYTE, root.getPrimitiveType("Byte"));
        assertEquals( MetadataRoot.CHARACTER, root.getPrimitiveType("Character"));
        assertEquals( MetadataRoot.DATE, root.getPrimitiveType("Date"));
        assertEquals( MetadataRoot.DOUBLE, root.getPrimitiveType("Double"));
        assertEquals( MetadataRoot.FLOAT, root.getPrimitiveType("Float"));
        assertEquals( MetadataRoot.INTEGER, root.getPrimitiveType("Integer"));
        assertEquals( MetadataRoot.LONG, root.getPrimitiveType("Long"));
        assertEquals( MetadataRoot.SHORT, root.getPrimitiveType("Short"));
        assertEquals( MetadataRoot.STRING, root.getPrimitiveType("String"));
        assertEquals( MetadataRoot.BLOB, root.getPrimitiveType("Blob"));
        assertEquals( MetadataRoot.CLOB, root.getPrimitiveType("Clob"));

    }

    private MetadataRoot getNewRoot()  {
        return MetadataRoot.newInstance();
    }
}
