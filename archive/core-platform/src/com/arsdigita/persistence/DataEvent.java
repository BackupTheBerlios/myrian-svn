/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

/**
 * DataEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/08/15 $
 **/

abstract class DataEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataEvent.java#6 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    DataObjectImpl m_object;

    public DataEvent(DataObjectImpl object) {
        m_object = object;
    }

    void invoke(DataObserver observer) {
        if (DataObjectImpl.s_log.isDebugEnabled()) {
            DataObjectImpl.s_log.debug(this);
        }
        doInvoke(observer);
    }

    abstract void doInvoke(DataObserver observer);

    abstract String getName();

    final void schedule() { m_object.scheduleObserver(this); }

    final void fire() { m_object.fireObserver(this); }

    public String toString() {
        return "observer event: " + m_object + " " + getName();
    }
}

abstract class PropertyEvent extends DataEvent {

    String m_property;

    public PropertyEvent(DataObjectImpl object, String property) {
        super(object);
        m_property = property;
    }

}

class SetEvent extends PropertyEvent {

    private Object m_old;
    private Object m_new;

    public SetEvent(DataObjectImpl object, String property, Object oldValue,
                    Object newValue) {
        super(object, property);
        m_old = oldValue;
        m_new = newValue;
    }

    void doInvoke(DataObserver observer) {
        observer.set(m_object, m_property, m_old, m_new);
    }


    String getName() { return " set " + m_property; }
}

class AddEvent extends PropertyEvent {

    private DataObject m_value;

    public AddEvent(DataObjectImpl object, String property, DataObject value) {
        super(object, property);
        m_value = value;
    }

    void doInvoke(DataObserver observer) {
        observer.add(m_object, m_property, m_value);
    }

    String getName() { return " add " + m_property; }
}

class RemoveEvent extends PropertyEvent {

    private DataObject m_value;

    public RemoveEvent(DataObjectImpl object, String property,
                       DataObject value) {
        super(object, property);
        m_value = value;
    }

    void doInvoke(DataObserver observer) {
        observer.remove(m_object, m_property, m_value);
    }

    String getName() { return " remove " + m_property; }
}

class ClearEvent extends PropertyEvent {

    public ClearEvent(DataObjectImpl object, String property) {
        super(object, property);
    }

    void doInvoke(DataObserver observer) {
        observer.clear(m_object, m_property);
    }

    String getName() { return " clear " + m_property; }
}

interface BeforeEvent {
    // returns the corresponding after event class
    Class getAfter();
}

interface AfterEvent {
    // returns the corresponding before event class
    Class getBefore();
}

abstract class ObjectDataEvent extends DataEvent {

    public ObjectDataEvent(DataObjectImpl object) { super(object); }

    public int hashCode() { return m_object.hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof ObjectDataEvent) {
            ObjectDataEvent other = (ObjectDataEvent) o;
            return this.m_object.equals(other.m_object) &&
                this.getClass().equals(other.getClass());
        }

        return false;
    }
}

class BeforeSaveEvent extends ObjectDataEvent implements BeforeEvent {

    public BeforeSaveEvent(DataObjectImpl object) {
        super(object);
    }

    public Class getAfter() { return AfterSaveEvent.class; }

    void doInvoke(DataObserver observer) {
        if (!m_object.isDeleted()) {
            observer.beforeSave(m_object);
        }
    }

    String getName() { return " before save"; }
}

class AfterSaveEvent extends ObjectDataEvent implements AfterEvent {

    public AfterSaveEvent(DataObjectImpl object) {
        super(object);
    }

    public Class getBefore() { return BeforeSaveEvent.class; }

    void doInvoke(DataObserver observer) {
        if (!m_object.isDeleted()) {
            observer.afterSave(m_object);
        }
    }

    String getName() { return " after save"; }
}

class BeforeDeleteEvent extends ObjectDataEvent implements BeforeEvent {

    public BeforeDeleteEvent(DataObjectImpl object) {
        super(object);
    }

    public Class getAfter() { return AfterDeleteEvent.class; }

    void doInvoke(DataObserver observer) {
        observer.beforeDelete(m_object);
    }

    String getName() { return " before delete"; }
}

class AfterDeleteEvent extends ObjectDataEvent implements AfterEvent {

    public AfterDeleteEvent(DataObjectImpl object) {
        super(object);
    }

    public Class getBefore() { return BeforeDeleteEvent.class; }

    void doInvoke(DataObserver observer) {
        observer.afterDelete(m_object);
    }

    String getName() { return " after delete"; }
}
