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

import java.util.*;

/**
 * MultiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class MultiSet {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/oql/MultiSet.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
