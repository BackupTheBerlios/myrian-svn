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
import com.arsdigita.persistence.metadata.ObjectType;
import junit.framework.*;
import java.util.*;

public abstract class DataCollectionTest extends DataQueryTest  {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DataCollectionTest.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public DataCollectionTest(String name) {
        super(name);
    }

    public void testGetDataObject()
    {
        DataCollection allItems = getDefaultCollection();
        int count = 0;
        while (allItems.next()) 
        {
            DataObject obj = allItems.getDataObject();
            assertEquals( "Somehow failed to retrieve correct DataObject", 
                getDefaultObjectType(), obj.getObjectType());
            count++;
        }
        assertTrue( "No data objects?", count > 0);
    }

    public void testGetObjectType()
    {

        DataCollection allItems = getDefaultCollection();
        int count = 0;
        while (allItems.next()) 
        {
            ObjectType type = allItems.getObjectType();
            assertEquals( "Somehow failed to retrieve correct object type.", getDefaultObjectType(), type);
            count++;
        } 

        assertTrue( "No data objects?", count > 0);
    }

    protected abstract DataCollection getDefaultCollection();
    protected abstract ObjectType getDefaultObjectType();

}

