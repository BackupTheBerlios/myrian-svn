package com.redhat.persistence.oql;

import java.util.*;

/**
 * MultiMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/24 $
 **/

class MultiMap {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/MultiMap.java#3 $ by $Author: rhs $, $DateTime: 2004/02/24 10:13:24 $";

    private List m_keys = new ArrayList();
    private Map m_lists = new HashMap();

    List keys() {
        return m_keys;
    }

    List get(Object key) {
        return get(key, Collections.EMPTY_LIST);
    }

    List get(Object key, List dephault) {
        if (m_lists.containsKey(key)) {
            return (List) m_lists.get(key);
        } else {
            return dephault;
        }
    }

    boolean contains(Object key) {
        return m_lists.containsKey(key);
    }

    boolean isEmpty() {
        return m_lists.isEmpty();
    }

    void add(Object key, Object value) {
        List values = (List) m_lists.get(key);
        if (values == null) {
            values = new ArrayList();
            m_lists.put(key, values);
            m_keys.add(key);
        }
        values.add(value);
    }

    void addAll(Object key, List values) {
        for (Iterator it = values.iterator(); it.hasNext(); ) {
            add(key, it.next());
        }
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
