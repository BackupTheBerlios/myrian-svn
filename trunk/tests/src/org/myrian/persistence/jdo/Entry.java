/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.jdo;

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
