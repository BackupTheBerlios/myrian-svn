/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import org.apache.commons.collections.list.*;

import java.util.*;

/**
 * MultiMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/04/07 $
 **/

class MultiMap {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/MultiMap.java#7 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

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
