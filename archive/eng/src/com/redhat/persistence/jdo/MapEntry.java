package com.redhat.persistence.jdo;

import java.util.Map;

public abstract class MapEntry implements Map.Entry {

    public abstract Object getKey();

    public abstract Object getVal();
    public abstract void setVal(Object value);

    public Object getValue() {
        return getVal();
    }

    public Object setValue(Object value) {
        Object oldValue = getVal();
        setVal(value);
        return oldValue;
    }

    public int hashCode() {
        Object key = getKey();
        Object value = getVal();

        return (key==null ? 0 : key.hashCode()) +
            (value==null ? 0 : value.hashCode());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) { return false; }

        Object key   = getKey();
        Object value = getVal();

        Map.Entry en = (Map.Entry) obj;
        Object enKey = en.getKey();
        Object enValue = en.getValue();

        return
            (key   == null ? enKey   == null :   key.equals(enKey))
            &&
            (value == null ? enValue == null : value.equals(enValue));
    }
}
