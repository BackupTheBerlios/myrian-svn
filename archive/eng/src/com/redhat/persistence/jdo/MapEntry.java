package com.redhat.persistence.jdo;

import java.util.Map;

final class MapEntry implements Map.Entry {
    private Object container;

    private Object key;
    private Object value;

    MapEntry() {}

    MapEntry(Object container, Object key) {
        this.container = container;
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Object setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    public int hashCode() {
        return (key==null ? 0 : key.hashCode()) +
            (value==null ? 0 : value.hashCode());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) { return false; }

        Map.Entry en = (Map.Entry) obj;
        Object enKey = en.getKey();
        Object enValue = en.getValue();

        return
            (key   == null ? enKey   == null :   key.equals(enKey))
            &&
            (value == null ? enValue == null : value.equals(enValue));
    }
}
