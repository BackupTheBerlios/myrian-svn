package com.redhat.persistence.jdo;

import java.util.Map;

public class MapEntry implements Map.Entry {

    private Object container;
    private Object key;
    private Object val;

    MapEntry() {}

    MapEntry(Object container, Object key) {
        this.container = container;
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return val;
    }

    public Object setValue(Object value) {
        Object oldValue = val;
        val = value;
        return oldValue;
    }

    public int hashCode() {
        return (key==null ? 0 : key.hashCode()) +
               (val==null ? 0 : val.hashCode());
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) { return false; }
    
        Map.Entry en = (Map.Entry) obj;
        Object enKey = en.getKey();
        Object enVal = en.getValue();
    
        return
            (key == null ? enKey == null : key.equals(enKey))
            &&
            (val == null ? enVal == null : val.equals(enVal));
    }

    public String toString() {
        return "MapEntry <container=" + container + "; key=" + key + ">";
    }
}
