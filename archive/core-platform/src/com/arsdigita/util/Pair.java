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

package com.arsdigita.util;

import java.util.Map;

/**
 * <p>
 * The simplest possible implementation of <code>Map.Entry</code>. Instances
 * of this class contains references to the <code>key</code> and
 * <code>value</code> set in the constructor.
 * </p>
 *
 * @author David Lutterkort
 * @version $Revision: #4 $ $Date: 2002/12/11 $
 */
public class Pair implements Map.Entry, Cloneable {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/Pair.java#4 $ by $Author: dennis $, $DateTime: 2002/12/11 13:49:53 $";

    private Object m_key;
    private Object m_value;

    /**
     * <p>
     * Creates a new <code>Pair</code> instance.
     * </p>
     *
     * @param key the key for this pair.
     * @param value the value for this pair.
     */
    public Pair(Object key, Object value) {
        m_key = key;
        m_value = value;
    }

    /**
     * <p>
     * Return a shallow copy of this pair. The key and value of the new pair
     * refer to the same objects as the key and value in the pair being
     * cloned.
     * </p>
     *
     * @return Object A new pair, referring to the same key and value
     */
    protected Object clone() {
        return new Pair(m_key, m_value);
    }

    /**
     * <p>
     * Returns the key corresponding to this pair.
     * </p>
     *
     * @return Object The key for this pair.
     */
    public final Object getKey() {
        return m_key;
    }

    /**
     * <p>
     * Returns the value corresponding to this pair.
     * </p>
     *
     * @return Object The value for this pair.
     */
    public final Object getValue() {
        return m_value;
    }

    /**
     * <p>
     * Replaces the value corresponding to this pair with the specified
     * value.
     * </p>
     *
     * @param new value to be stored in this entry.
     *
     * @return Object Old value corresponding to the entry.
     */
    public Object setValue(Object value) {
        Object oldValue = m_value;

        m_value = value;

        return oldValue;
    }

    /**
     * <p>
     * Compare the specified object with this pair. Returns true if the given
     * object is also a <code>Map.Entry</code> and its key and value are
     * equal to those of this pair. More formally, two entries e1 and e2
     * represent the same mapping if
     * <pre>
     * (e1.getKey()==null ?
     *     e2.getKey()==null : e1.getKey().equals(e2.getKey()))  &&
     * (e1.getValue()==null ?
     *     e2.getValue()==null : e1.getValue().equals(e2.getValue()))
     * </pre>
     * </p>
     *
     * @param o object to be compared for equality with this pair.
     *
     * @return boolean true if the specified object is equal to this pair as a
     *         map entry.
     */
    public boolean equals(Object o) {
        boolean rv = false;

        if (!(o instanceof Map.Entry)) {
            rv = false;
        } else {
            Map.Entry e = (Map.Entry) o;
            rv = (
                  (m_key == null ? e.getKey() == null : m_key.equals(e.getKey())) &&
                  (m_value == null ? e.getValue() == null : m_value.equals(e.getValue()))
                  );
        }

        return rv;
    }

    /**
     * <p>
     * The hash code for this pair. The hash code is the bitwise exclusive or
     * of the hash codes of the key and the value. If either of these entries
     * is <code>null</code>, its hash code is taken to be <code>0</code> in
     * the exclusive or.
     * </p>
     *
     * @return int The hash code of this pair.
     */
    public int hashCode() {
        return (m_key == null ? 0 : m_key.hashCode()) ^
            (m_value == null ? 0 : m_value.hashCode());
    }

    /**
     * <p>
     * Convert this pair to a <code>String</code>. The returned string is of
     * the form <code>key=value</code> where <code>key</code> and
     * <code>value</code> are the entries in this pair, converted to
     * <code>String</code>.
     * </p>
     *
     * @return String of the form <code>key=value</code>
     */
    public String toString() {
        return m_key + "=" + m_value;
    }
}
