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
import com.arsdigita.persistence.metadata.ObjectType;
import junit.framework.*;
import java.util.*;

public abstract class DataCollectionTest extends DataQueryTest  {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/DataCollectionTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
