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

public class Entry implements Map.Entry {

    private Object m_key;
    private Object m_value;

    public Entry(Object key, Object value) {
        m_key = key;
        m_value = value;
    }

    public Object getKey() {
        return m_key;
    }

    public Object getValue() {
        return m_value;
    }

    public Object setValue(Object newValue) {
        Object oldValue = m_value;
        m_value = newValue;
        return oldValue;
    }

    public int hashCode() {
        return (m_key==null ? 0 : m_key.hashCode()) +
               (m_value==null ? 0 : m_value.hashCode());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) { return false; }

        Map.Entry en = (Map.Entry) obj;
        Object enKey = en.getKey();
        Object enVal = en.getValue();

        return
            (m_key == null   ? enKey == null : m_key.equals(enKey))
            &&
            (m_value == null ? enVal == null : m_value.equals(enVal));
    }

    public String toString() {
        return "<key=" + m_key + "; value=" + m_value + ">";
    }
}
