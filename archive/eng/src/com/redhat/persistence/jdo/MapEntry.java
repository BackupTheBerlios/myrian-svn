/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.jdo;

import java.util.Map;

public class MapEntry implements Map.Entry {

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

    public Object setValue(Object newValue) {
        Object oldValue = value;
        value = newValue;
        return oldValue;
    }

    public int hashCode() {
        return (key==null ? 0 : key.hashCode()) ^
               (value==null ? 0 : value.hashCode());
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) { return false; }
    
        Map.Entry en = (Map.Entry) obj;
        Object enKey = en.getKey();
        Object enVal = en.getValue();
    
        return
            (key == null ? enKey == null : key.equals(enKey))
            &&
            (value == null ? enVal == null : value.equals(enVal));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MapEntry <container=").append(container);
        sb.append("; key=").append(key).append(">");
        return sb.toString();
    }
}
