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

/**
 * DataEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/03/28 $
 **/

abstract class DataEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataEvent.java#2 $ by $Author: ashah $, $DateTime: 2003/03/28 12:30:01 $";

    DataObjectImpl m_object;

    public DataEvent(DataObjectImpl object) {
        m_object = object;
    }

    protected abstract void invoke(DataObserver observer);

    abstract String getName();

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

    protected void invoke(DataObserver observer) {
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

    protected void invoke(DataObserver observer) {
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

    protected void invoke(DataObserver observer) {
        observer.remove(m_object, m_property, m_value);
    }

    String getName() { return " remove " + m_property; }
}

class ClearEvent extends PropertyEvent {

    public ClearEvent(DataObjectImpl object, String property) {
        super(object, property);
    }

    protected void invoke(DataObserver observer) {
        observer.clear(m_object, m_property);
    }

    String getName() { return " clear " + m_property; }
}

class BeforeSaveEvent extends DataEvent {

    public BeforeSaveEvent(DataObjectImpl object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.beforeSave(m_object);
    }

    String getName() { return " before save"; }
}

class AfterSaveEvent extends DataEvent {

    public AfterSaveEvent(DataObjectImpl object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.afterSave(m_object);
    }

    String getName() { return " after save"; }
}

class BeforeDeleteEvent extends DataEvent {

    public BeforeDeleteEvent(DataObjectImpl object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.beforeDelete(m_object);
    }

    String getName() { return " before delete"; }
}

class AfterDeleteEvent extends DataEvent {

    public AfterDeleteEvent(DataObjectImpl object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.afterDelete(m_object);
    }

    String getName() { return " after delete"; }
}
