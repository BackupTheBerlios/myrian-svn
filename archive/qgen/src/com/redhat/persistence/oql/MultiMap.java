package com.redhat.persistence.oql;

import java.util.*;

/**
 * MultiMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/02/21 $
 **/

class MultiMap {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/MultiMap.java#1 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private List m_keys = new ArrayList();
    private Map m_lists = new HashMap();

    List keys() {
        return m_keys;
    }

    List get(Object key) {
        return (List) m_lists.get(key);
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

}
