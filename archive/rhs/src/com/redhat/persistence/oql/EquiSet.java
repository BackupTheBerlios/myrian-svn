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

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.commons.collections.list.*;
import org.apache.log4j.Logger;

/**
 * EquiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

class EquiSet {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/EquiSet.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private static final Logger s_log = Logger.getLogger(EquiSet.class);

    private Generator m_generator;

    private Map m_nodes = new HashMap();
    private List m_partitions = new ArrayList();
    private List m_free = new ArrayList();

    private List m_frames = SetUniqueList.decorate(new ArrayList());
    private List m_framesets = new ArrayList();

    EquiSet(Generator generator) {
        m_generator = generator;
    }

    int size() {
        return m_nodes.size();
    }

    void clear() {
        m_nodes.clear();
        m_free.clear();
        for (int i = 0; i < m_partitions.size(); i++) {
            getPartition(i).clear();
            m_free.add(new Integer(i));
        }
        m_frames.clear();
        m_framesets.clear();
    }

    boolean isEmpty() {
        return m_nodes.isEmpty();
    }

    List get(Object nd) {
        Integer idx = (Integer) m_nodes.get(nd);
        if (idx == null) {
            return null;
        } else {
            return getPartition(idx);
        }
    }

    Integer partition(Object nd) {
        return (Integer) m_nodes.get(nd);
    }

    List getPartitions() {
        return m_partitions;
    }

    List getPartition(Integer idx) {
        return getPartition(idx.intValue());
    }

    List getPartition(int i) {
        return (List) m_partitions.get(i);
    }

    private int allocatePartition() {
        int result;
        if (m_free.isEmpty()) {
            result = m_partitions.size();
            m_partitions.add(new ArrayList());
        } else {
            result = ((Integer) m_free.remove(m_free.size() - 1)).intValue();
        }
        return result;
    }

    private List m_equals = new ArrayList();

    boolean equate(QValue a, QValue b) {
        m_frames.add(a.getFrame());
        m_frames.add(b.getFrame());
        m_equals.clear();
        m_equals.add(a);
        if (!a.equals(b)) {
            m_equals.add(b);
        }
        return add(m_equals);
    }

    boolean equate(Object a, Object b) {
        m_equals.clear();
        m_equals.add(a);
        if (!a.equals(b)) {
            m_equals.add(b);
        }
        return add(m_equals);
    }

    void collapse() {
        while (doCollapse()) {};
    }

    private MultiMap m_collated = new MultiMap();
    private MultiMap m_columns = new MultiMap();
    private List m_keys = new ArrayList();

    private boolean doCollapse() {
        m_collated.clear();
        m_columns.clear();
        for (int i = 0; i < m_frames.size(); i++) {
            QFrame frame = (QFrame) m_frames.get(i);
            m_keys.clear();
            keys(frame, m_keys);
            for (int j = 0; j < m_keys.size(); j++) {
                Object key = m_keys.get(j);
                m_collated.add(key, frame);
                m_columns.addAll(key, frame.getColumns());
            }
        }

        boolean modified = false;

        List keys = m_collated.keys();
        for (int i = 0; i < keys.size(); i++) {
            Object key = keys.get(i);
            List frames = m_collated.get(key);
            List cols = m_columns.get(key);
            for (int j = 0; j < cols.size(); j++) {
                String col = (String) cols.get(j);
                m_equals.clear();
                for (int k = 0; k < frames.size(); k++) {
                    QFrame frame = (QFrame) frames.get(k);
                    m_equals.add(frame.getValue(col));
                }
                modified |= add(m_equals);
            }
        }

        if (!modified) {
            m_framesets.clear();
            for (int i = 0; i < keys.size(); i++) {
                m_framesets.add(m_collated.get(keys.get(i)));
            }
        }

        return modified;
    }

    void keys(QFrame frame, List result) {
        Table t = m_generator.getRoot().getTable(frame.getTable());
        if (t == null) { return; }
        OUTER: for (Iterator it = t.getConstraints().iterator();
                    it.hasNext(); ) {
            Constraint c = (Constraint) it.next();
            if (!(c instanceof UniqueKey)) { continue; }
            UniqueKey uk = (UniqueKey) c;
            Column[] cols = uk.getColumns();
            Object key = uk;
            for (int i = 0; i < cols.length; i++) {
                if (!frame.hasValue(cols[i].getName())) {
                    continue OUTER;
                }
                QValue v = frame.getValue(cols[i].getName());
                Object id = m_nodes.get(v);
                if (id == null) { id = v; }
                if (key == null) {
                    key = id;
                } else {
                    key = new CompoundKey(id, key);
                }
            }
            result.add(key);
        }
    }

    List getFrameSets() {
        return m_framesets;
    }

    private List m_from = SetUniqueList.decorate(new ArrayList());

    boolean add(List equal) {
        Integer to = null;
        List added = null;
        int addedidx = -1;
        m_from.clear();

        for (int i = 0; i < equal.size(); i++) {
            Object o = equal.get(i);
            Integer idx = (Integer) m_nodes.get(o);
            if (idx == null) {
                if (added == null) {
                    addedidx = allocatePartition();
                    added = getPartition(addedidx);
                }
                added.add(o);
            } else if (to == null) {
                to = idx;
            } else if (to.equals(idx)) {
                // do nothing
            } else {
                List top = getPartition(to);
                List fromp = getPartition(idx);
                if (fromp.size() > top.size()) {
                    m_from.add(to);
                    to = idx;
                } else {
                    m_from.add(idx);
                }
            }
        }

        if (to == null) {
            if (added == null) {
                return false;
            } else {
                partitionAll(added, new Integer(addedidx));
                return true;
            }
        }

        List top = getPartition(to);

        boolean modified = false;

        if (added != null) {
            if (added.size() > top.size()) {
                m_from.add(to);
                to = new Integer(addedidx);
                top = added;
                partitionAll(added, to);
                modified = true;
            } else {
                m_from.add(new Integer(addedidx));
            }
        }

        for (int i = 0; i < m_from.size(); i++) {
            Integer idx = (Integer) m_from.get(i);
            List from = getPartition(idx);
            for (int j = 0; j < from.size(); j++) {
                Object o = from.get(j);
                top.add(o);
                m_nodes.put(o, to);
            }
            from.clear();
            m_free.add(idx);
            modified = true;
        }

        return modified;
    }

    private void partitionAll(List p, Integer idx) {
        for (int i = 0; i < p.size(); i++) {
            m_nodes.put(p.get(i), idx);
        }
    }

    boolean addAll(EquiSet equiset) {
        boolean modified = false;
        for (int i = 0; i < equiset.m_partitions.size(); i++) {
            List p = equiset.getPartition(i);
            if (!p.isEmpty()) {
                modified |= add(p);
            }
        }
        m_frames.addAll(equiset.m_frames);
        return modified;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        boolean first = true;
        for (int i = 0; i < m_partitions.size(); i++) {
            List p = getPartition(i);
            if (p.isEmpty()) { continue; }
            if (first) {
                first = false;
            } else {
                buf.append(" | ");
            }
            for (int j = 0; j < p.size(); j++) {
                buf.append(p.get(j));
                if (j < p.size() - 1) {
                    buf.append(", ");
                }
            }
        }
        buf.append("}");
        return buf.toString();
    }

}
