package com.redhat.persistence.oql;

import java.util.*;

/**
 * MultiMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/03 $
 **/

class MultiMap {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/MultiMap.java#4 $ by $Author: rhs $, $DateTime: 2004/03/03 12:16:16 $";

    private List m_keys = new ArrayList();
    private Map m_sets = new HashMap();

    List keys() {
        return m_keys;
    }

    Set get(Object key) {
        return get(key, Collections.EMPTY_SET);
    }

    Set get(Object key, Set dephault) {
        if (m_sets.containsKey(key)) {
            return (Set) m_sets.get(key);
        } else {
            return dephault;
        }
    }

    boolean contains(Object key) {
        return m_sets.containsKey(key);
    }

    boolean isEmpty() {
        return m_sets.isEmpty();
    }

    void add(Object key, Object value) {
        Set values = (Set) m_sets.get(key);
        if (values == null) {
            values = new HashSet();
            m_sets.put(key, values);
            m_keys.add(key);
        }
        values.add(value);
    }

    void addAll(Object key, Collection values) {
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
