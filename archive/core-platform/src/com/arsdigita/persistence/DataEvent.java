package com.arsdigita.persistence;

/**
 * DataEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 **/

abstract class DataEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataEvent.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

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
