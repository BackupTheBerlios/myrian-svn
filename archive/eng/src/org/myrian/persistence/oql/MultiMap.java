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
package org.myrian.persistence.oql;

import org.apache.commons.collections.list.*;

import java.util.*;

/**
 * MultiMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

class MultiMap {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/oql/MultiMap.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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
