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
package com.redhat.persistence.oql;

import org.apache.commons.collections.list.*;

import java.util.*;

/**
 * MultiMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class MultiMap {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/MultiMap.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private List m_keys = new ArrayList();
    private Map m_values = new HashMap();
    private List m_free = new ArrayList();
    private int m_size = 0;

    List keys() {
        return m_keys;
    }

    int size() {
        return m_size;
    }

    List get(Object key) {
        return get(key, Collections.EMPTY_LIST);
    }

    List get(Object key, List dephault) {
        if (m_values.containsKey(key)) {
            return (List) m_values.get(key);
        } else {
            return dephault;
        }
    }

    boolean contains(Object key) {
        return m_values.containsKey(key);
    }

    boolean isEmpty() {
        return m_values.isEmpty();
    }

    void add(Object key, Object value) {
        List values = (List) m_values.get(key);
        if (values == null) {
            values = allocateValues();
            m_values.put(key, values);
            m_keys.add(key);
        }
        if (values.add(value)) {
            m_size++;
        };
    }

    void addAll(Object key, List values) {
        for (int i = 0; i < values.size(); i++) {
            add(key, values.get(i));
        }
    }

    private List allocateValues() {
        if (m_free.isEmpty()) {
            return SetUniqueList.decorate(new ArrayList());
        } else {
            List result = (List) m_free.remove(m_free.size() - 1);
            result.clear();
            return result;
        }
    }

    void clear() {
        m_keys.clear();
        for (Iterator it = m_values.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            List values = (List) me.getValue();
            values.clear();
            m_free.add(values);
            it.remove();
        }
        m_size = 0;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (Iterator it = keys().iterator(); it.hasNext(); ) {
            Object key = it.next();
            buf.append(key + "=" + get(key));
            if (it.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

}
