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

import java.util.*;

/**
 * MultiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/02 $
 **/

class MultiSet {

    public final static String versionId = "$Id: //users/rhs/persistence/test/src/com/redhat/persistence/oql/MultiSet.java#1 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private Map m_entries;

    MultiSet() {
        m_entries = new HashMap();
    }

    MultiSet(MultiSet ms) {
        m_entries = new HashMap();
        m_entries.putAll(ms.m_entries);
    }

    public void add(Object obj) {
        Integer count = (Integer) m_entries.get(obj);
        if (count == null) {
            count = new Integer(1);
        } else {
            count = new Integer(count.intValue() + 1);
        }
        m_entries.put(obj, count);
    }

    public void addAll(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            add(it.next());
        }
    }

    public void remove(Object obj) {
        Integer count = (Integer) m_entries.get(obj);
        if (count != null) {
            count = new Integer(count.intValue() - 1);
            if (count.intValue() == 0) {
                m_entries.remove(obj);
            } else {
                m_entries.put(obj, count);
            }
        }
    }

    public void removeAll(MultiSet ms) {
        Collection entries = ms.m_entries.entrySet();
        for (Iterator it = entries.iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Object obj = me.getKey();
            Integer count = (Integer) me.getValue();
            for (int i = 0; i < count.intValue(); i++) {
                remove(obj);
            }
        }
    }

    public int hashCode() {
        return m_entries.hashCode();
    }

    public boolean equals(Object o) {
        MultiSet ms = (MultiSet) o;
        return m_entries.equals(ms.m_entries);
    }

    public String toString() {
        return m_entries.toString();
    }

}
