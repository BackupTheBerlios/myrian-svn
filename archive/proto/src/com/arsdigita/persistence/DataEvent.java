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
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

abstract class DataEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataEvent.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    DataObject m_object;

    public DataEvent(DataObject object) {
        m_object = object;
    }

    protected abstract void invoke(DataObserver observer);

}

abstract class PropertyEvent extends DataEvent {

    String m_property;

    public PropertyEvent(DataObject object, String property) {
        super(object);
        m_property = property;
    }

}

class SetEvent extends PropertyEvent {

    private Object m_old;
    private Object m_new;

    public SetEvent(DataObject object, String property, Object oldValue,
                    Object newValue) {
        super(object, property);
        m_old = oldValue;
        m_new = newValue;
    }

    protected void invoke(DataObserver observer) {
        observer.set(m_object, m_property, m_old, m_new);
    }

}

class AddEvent extends PropertyEvent {

    private DataObject m_value;

    public AddEvent(DataObject object, String property, DataObject value) {
        super(object, property);
        m_value = value;
    }

    protected void invoke(DataObserver observer) {
        observer.add(m_object, m_property, m_value);
    }

}

class RemoveEvent extends PropertyEvent {

    private DataObject m_value;

    public RemoveEvent(DataObject object, String property, DataObject value) {
        super(object, property);
        m_value = value;
    }

    protected void invoke(DataObserver observer) {
        observer.remove(m_object, m_property, m_value);
    }

}

class ClearEvent extends PropertyEvent {

    public ClearEvent(DataObject object, String property) {
        super(object, property);
    }

    protected void invoke(DataObserver observer) {
        observer.clear(m_object, m_property);
    }

}

class BeforeSaveEvent extends DataEvent {

    public BeforeSaveEvent(DataObject object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.beforeSave(m_object);
    }

}

class AfterSaveEvent extends DataEvent {

    public AfterSaveEvent(DataObject object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.afterSave(m_object);
    }

}

class BeforeDeleteEvent extends DataEvent {

    public BeforeDeleteEvent(DataObject object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.beforeDelete(m_object);
    }

}

class AfterDeleteEvent extends DataEvent {

    public AfterDeleteEvent(DataObject object) {
        super(object);
    }

    protected void invoke(DataObserver observer) {
        observer.afterDelete(m_object);
    }

}
