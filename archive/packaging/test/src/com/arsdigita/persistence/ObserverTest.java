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

package com.arsdigita.persistence;

import java.util.*;
import java.math.*;

/**
 * ObserverTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class ObserverTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/ObserverTest.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public ObserverTest(String name) {
        super(name);
    }

    public static final String SET = "set";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String CLEAR = "clear";
    public static final String BEFORE_SAVE = "before save";
    public static final String AFTER_SAVE = "after save";
    public static final String BEFORE_DELETE = "before delete";
    public static final String AFTER_DELETE = "after delete";

    private static final String NAME = "name";
    private static final String PARENT = "parent";
    private static final String CHILDREN = "children";
    private static final String REQUIRED = "required";
    private static final String COLLECTION = "collection";

    private static final String VALUE = "Value";

    private static final DataObject createTest() {
        Session ssn = SessionManager.getSession();
        DataObject obj = ssn.create(new OID("test.Test", BigInteger.ZERO));
        obj.set(REQUIRED, ssn.create(new OID("test.Icle", BigInteger.ONE)));
        obj.save();
        return obj;
    }

    private static final DataObject createIcle() {
        Session ssn = SessionManager.getSession();
        return ssn.create(new OID("test.Icle", BigInteger.ZERO));
    }

    public void testSet() {
        DataObject data = createTest();
        TestObserver observer = new TestObserver();
        data.addObserver(observer);

        data.set(NAME, VALUE);

        assertEquals(1, observer.getEvents().size());
        assertEquals(SET, observer.getLastEvent());
        assertEquals(data, observer.getDataObject());
        assertEquals(NAME, observer.getProperty());
        assertEquals(null, observer.getPrevious());
        assertEquals(VALUE, observer.getValue());

        data.set(NAME, VALUE);

        assertEquals(2, observer.getEvents().size());
        assertEquals(SET, observer.getLastEvent());
        assertEquals(data, observer.getDataObject());
        assertEquals(NAME, observer.getProperty());
        assertEquals(VALUE, observer.getPrevious());
        assertEquals(VALUE, observer.getValue());
    }

    public void testAdd() {
        DataObject data = createTest();
        TestObserver observer = new TestObserver();
        data.addObserver(observer);

        DataAssociation assn = (DataAssociation) data.get(COLLECTION);
        DataObject icle = createIcle();
        assn.add(icle);

        assertEquals(1, observer.getEvents().size());
        assertEquals(ADD, observer.getLastEvent());
        assertEquals(data, observer.getDataObject());
        assertEquals(COLLECTION, observer.getProperty());
        assertEquals(icle, observer.getValue());
    }

    public void testRemove() {
        DataObject data = createTest();
        TestObserver observer = new TestObserver();
        data.addObserver(observer);

        DataAssociation assn = (DataAssociation) data.get(COLLECTION);
        DataObject icle = createIcle();
        assn.remove(icle);

        assertEquals(1, observer.getEvents().size());
        assertEquals(REMOVE, observer.getLastEvent());
        assertEquals(data, observer.getDataObject());
        assertEquals(COLLECTION, observer.getProperty());
        assertEquals(icle, observer.getValue());
    }

    public void testBeforeAndAfterSave() {
        DataObject data = createTest();

        TestObserver observer = new TestObserver();
        data.addObserver(observer);
        data.save();

        assertEquals(2, observer.getEvents().size());
        assertEquals(BEFORE_SAVE, observer.getFirstEvent());
        assertEquals(AFTER_SAVE, observer.getLastEvent());
        assertEquals(data, observer.getDataObject());
    }

    public void testBeforeAndAfterDelete() {
        DataObject data = createTest();

        Session ssn = SessionManager.getSession();
        data = ssn.retrieve(new OID("test.Test", BigInteger.ZERO));
        TestObserver observer = new TestObserver();
        data.addObserver(observer);

        data.delete();

        assertEquals(2, observer.getEvents().size());
        assertEquals(BEFORE_DELETE, observer.getFirstEvent());
        assertEquals(AFTER_DELETE, observer.getLastEvent());
        assertEquals(data, observer.getDataObject());
    }

    public void testLoopDetection() {
        DataObject data = createTest();

        data.addObserver(new LoopDetector());
        try {
            data.save();
            fail("Loop detector failed silently.");
        } catch (PersistenceException pe) {
            assertTrue("Incorrect exception generated by loop detector.",
                       pe.getMessage().startsWith("Loop detected"));
        }
    }

    private class LoopDetector extends DataObserver {

        private boolean m_set = false;

        public void set(DataObject data, String property, Object previous,
                        Object value) {
            m_set = true;
            data.set(property, value);
        }

        private boolean m_add = false;

        public void add(DataObject data, String property, DataObject value) {
            DataAssociation assn = (DataAssociation) data.get(property);
            assertTrue("Loop was permitted", !m_add);
            m_add = true;
            assn.add(value);
        }

        private boolean m_remove = false;

        public void remove(DataObject data, String property,
                           DataObject value) {
            DataAssociation assn = (DataAssociation) data.get(property);
            assertTrue("Loop was permitted", !m_remove);
            m_remove = true;
            assn.remove(value);
        }

        private boolean m_clear = false;

        public void clear(DataObject data, String property) {
            DataAssociation assn = (DataAssociation) data.get(property);
            assertTrue("Loop was permitted", !m_clear);
            m_clear = true;
            assn.clear();
        }

        private boolean m_beforeSave = false;

        public void beforeSave(DataObject data) {
            assertTrue("Loop was permitted", !m_beforeSave);
            m_beforeSave = true;
            data.save();
        }

        private boolean m_afterSave = false;

        public void afterSave(DataObject data) {
            assertTrue("Loop was permitted", !m_afterSave);
            m_afterSave = true;
            data.save();
        }

        private boolean m_beforeDelete = false;

        public void beforeDelete(DataObject data) {
            assertTrue("Loop was permitted", !m_beforeDelete);
            m_beforeDelete = true;
            data.delete();
        }

        private boolean m_afterDelete = false;

        public void afterDelete(DataObject data) {
            assertTrue("Loop was permitted", !m_afterDelete);
            m_afterDelete = true;
            data.delete();
        }

    }

}

class TestObserver extends DataObserver {

    private List m_events = new ArrayList();
    private DataObject m_data;
    private String m_property;
    private Object m_previous;
    private Object m_value;

    public List getEvents() {
        return m_events;
    }

    public String getFirstEvent() {
        return (String) m_events.get(0);
    }

    public String getLastEvent() {
        return (String) m_events.get(m_events.size() - 1);
    }

    public DataObject getDataObject() {
        return m_data;
    }

    public String getProperty() {
        return m_property;
    }

    public Object getPrevious() {
        return m_previous;
    }

    public Object getValue() {
        return m_value;
    }

    public void set(DataObject data, String property, Object oldValue,
                    Object newValue) {
        m_events.add(ObserverTest.SET);
        m_data = data;
        m_property = property;
        m_previous = oldValue;
        m_value = newValue;
    }

    public void add(DataObject data, String property, DataObject value) {
        m_events.add(ObserverTest.ADD);
        m_data = data;
        m_property = property;
        m_value = value;
    }

    public void remove(DataObject data, String property, DataObject value) {
        m_events.add(ObserverTest.REMOVE);
        m_data = data;
        m_property = property;
        m_value = value;
    }

    public void clear(DataObject data, String property) {
        m_events.add(ObserverTest.CLEAR);
        m_data = data;
        m_property = property;
    }

    public void beforeSave(DataObject data) {
        m_events.add(ObserverTest.BEFORE_SAVE);
        m_data = data;
    }

    public void afterSave(DataObject data) {
        m_events.add(ObserverTest.AFTER_SAVE);
        m_data = data;
    }

    public void beforeDelete(DataObject data) {
        m_events.add(ObserverTest.BEFORE_DELETE);
        m_data = data;
    }

    public void afterDelete(DataObject data) {
        m_events.add(ObserverTest.AFTER_DELETE);
        m_data = data;
    }

}
