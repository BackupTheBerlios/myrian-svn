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

package com.arsdigita.persistence;

import junit.framework.TestCase;
import java.util.Map;

/**
 * DataContainerTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class DataContainerTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/DataContainerTest.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public DataContainerTest(String name) {
        super(name);
    }

    private DataContainer newData, oldData;
    private static String[] props = {"foo", "bar", "baz"};
    private static String[] values = {"fooValue", "barValue", null};

    public void setUp() {
        newData = new DataContainer();
        oldData = new DataContainer();
        for (int i = 0; i < props.length; i++) {
            newData.set(props[i], values[i]);
            oldData.initProperty(props[i], values[i]);
        }
    }

    public void testGetProperty() {
        for (int i = 0; i < props.length; i++) {
            assertEquals("get failed on new container",
                         values[i], newData.get(props[i]));
            assertEquals("get failed on old container",
                         values[i], oldData.get(props[i]));
        }
    }

    public void testSetProperty() {
        for (int i = 0; i < props.length; i++) {
            newData.set(props[i], props[i] + "NewValue");
            oldData.set(props[i], props[i] + "NewValue");
        }

        for (int i = 0; i < props.length; i++) {
            assertEquals("set failed on new container",
                         props[i] + "NewValue", newData.get(props[i]));
            assertEquals("set failed on old container",
                         props[i] + "NewValue", oldData.get(props[i]));
        }
    }

    public void testHasProperty() {
        for (int i = 0; i < props.length; i++) {
            assertTrue("hasProperty failed on new container",
                   newData.hasProperty(props[i]));
            assertTrue("hasProperty failed on old container",
                   newData.hasProperty(props[i]));
        }
    }

    public void testGetProperties() {
        Map newMap = newData.getProperties();
        Map oldMap = oldData.getProperties();

        assertEquals("getProperties returned a Map of the wrong size",
                     props.length, newMap.size());
        assertEquals("getProperties returned a Map of the wrong size",
                     props.length, oldMap.size());

        for (int i = 0; i < props.length; i++) {
            assertEquals("getProperties returned a bad Map",
                         newData.get(props[i]), newMap.get(props[i]));
            assertEquals("getProperties returned a bad Map",
                         oldData.get(props[i]), oldMap.get(props[i]));
        }
    }

    public void testIsModified() {
        assertEquals("Container initialized with set " +
                     "should be modified.",
                     true, newData.isModified());
        assertEquals("Container initialized with initProperty " +
                     "should not be modified.",
                     false, oldData.isModified());

        oldData.set("foo", "newFooValue");
        assertEquals("After set is called isModified " +
                     "should return true.",
                     true, oldData.isModified());
    }

    public void testIsPropertyModified() {
        for (int i = 0; i < props.length; i++) {
            assertEquals("All new properties should be modified.",
                         true,
                         newData.isPropertyModified(props[i]));
            assertEquals("No old properties should be modified.",
                         false,
                         oldData.isPropertyModified(props[i]));
        }

        oldData.set("foo", "newFooValue");
        assertEquals("Property was not successfully set.",
                     true,
                     oldData.isPropertyModified("foo"));
    }

    public void testClear() {
        oldData.clear();
        newData.clear();

        for (int i = 0; i < props.length; i++) {
            assertEquals("clear failed on new container",
                         null, newData.get(props[i]));
            assertEquals("clear failed on old container",
                         null, oldData.get(props[i]));
        }

    }

    /**
     *  This tests the ability to set a DataContainer RoleReference to
     *
     */
    public void testNullRoleReferenceInInitProperty() throws Exception {
        oldData.initProperty("testRole", null);

        assertTrue(!oldData.isPropertyModified("testRole"));
        assertTrue(!oldData.isModified());

        // this needs some more work to make sure that you can modify
        // the association and that isModified and isPropertyModified still
        // works
    }


}
