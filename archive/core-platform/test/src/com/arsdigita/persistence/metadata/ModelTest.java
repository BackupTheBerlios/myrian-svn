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

package com.arsdigita.persistence.metadata;

import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;

/**
 * ModelTest    (Copyright 2001 ArsDigita Corporation)
 * 
 * <p> This class performs unit tests on com.arsdigita.persistence.metadatax.Model </p>
 *
 * @author <a href="mailto:jorris@arsdigita.com">jorris@arsdigita.com</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 * 
 * @see com.arsdigita.persistence.metadata.Model
 */

public class ModelTest extends TestCase
{

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/metadata/ModelTest.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public ModelTest(String name) {
        super(name);
    }

    public void testDataTypeMethods() {
        // Test basic addition, retrieval
        Model test = new Model("test");
        SimpleType jon =
            new SimpleType("jon", java.lang.Integer.class, Types.INTEGER) {
                    public int bindValue(PreparedStatement ps, int index,
                                         Object value, int jdbcType) {
                        // Do nothing
                        return 0;
                    }

                    public Object fetch(ResultSet rs, String column) {
                        return null;
                    }
                };
        test.addDataType(jon);
        DataType found = test.getDataType("jon");
        assertEquals( jon, found );
        assertEquals(test, found.getModel());

        assertTrue( test.getObjectType("jon") == null );

        // Try an ObjectType
        ObjectType gronk = new ObjectType("gronk");
        test.addDataType(gronk);
        assertEquals( gronk, test.getDataType("gronk"));
        assertEquals( gronk, test.getObjectType("gronk"));
        
        // See what happens if I had a Datatype to two different models!
        Model first = new Model("first");
        Model second = new Model("second");
        
        SimpleType type =
            new SimpleType("foo", java.lang.Integer.class, Types.INTEGER) {
                    public int bindValue(PreparedStatement ps, int index,
                                         Object value, int jdbcType) {
                        // Do nothing
                        return 0;
                    }

                    public Object fetch(ResultSet rs, String column) {
                        return null;
                    }
                };
        first.addDataType(type);
        try {
            second.addDataType(type);
            DataType problem = first.getDataType("foo");
            assertEquals(
                "Successfully added same DataType to two models, " +
                "thus breaking invariant!",
                first,
                problem.getModel());
        } catch (IllegalArgumentException e) {
            // This is good, it means our validation caught the buggy code.
        }
    }

    public void testDataOperationMethods() {
        // Basic functionality test.
        Model test = new Model("test"); 

        DataOperationType operation = new DataOperationType("optype", new Event());
        test.addDataOperationType(operation);
        assertEquals( operation, test.getDataOperationType("optype")); 
        assertEquals( "Model not properly set on DataOperation", test, operation.getModel());
    }                                                                  
}














