package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * EquiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/03/25 $
 **/

class EquiSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/EquiSet.java#5 $ by $Author: richardl $, $DateTime: 2004/03/25 09:49:17 $";

    private static final Logger s_log = Logger.getLogger(EquiSet.class);

    private Generator m_generator;

    private Map m_nodes = new HashMap();
    private List m_partitions = new ArrayList();
    private List m_free = new ArrayList();

    private Set m_frames = new HashSet();
    private List m_framesets = new ArrayList();

    EquiSet(Generator generator) {
        m_generator = generator;
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

    List get(QValue a) {
        Integer idx = (Integer) m_nodes.get(a);
        if (idx == null) {
            return null;
        } else {
            return getPartition(idx);
        }
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

    void collapse() {
        while (collapse(m_frames)) {};
    }

    private MultiMap m_collated = new MultiMap();
    private MultiMap m_columns = new MultiMap();
    private List m_keys = new ArrayList();

    boolean collapse(Collection frames) {
        m_collated.clear();
        m_columns.clear();
        for (Iterator it = frames.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            m_keys.clear();
            keys(frame, m_keys);
            for (int i = 0; i < m_keys.size(); i++) {
                Object key = m_keys.get(i);
                m_collated.add(key, frame);
                m_columns.addAll(key, frame.getColumns());
            }
        }

        boolean modified = false;

        List keys = m_collated.keys();
        for (int i = 0; i < keys.size(); i++) {
            Object key = keys.get(i);
            Set set = m_collated.get(key);
            Set cols = m_columns.get(key);
            for (Iterator it = cols.iterator(); it.hasNext(); ) {
                String col = (String) it.next();
                m_equals.clear();
                for (Iterator iter = set.iterator(); iter.hasNext(); ) {
                    QFrame frame = (QFrame) iter.next();
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
                Integer idx = (Integer) m_nodes.get(v);
                if (idx == null) {
                    continue OUTER;
                }
                if (key == null) {
                    key = idx;
                } else {
                    key = new CompoundKey(idx, key);
                }
            }
            result.add(key);
        }
    }

    List getFrameSets() {
        return m_framesets;
    }

    private List getPartition(Integer idx) {
        return getPartition(idx.intValue());
    }

    private List getPartition(int i) {
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

    private Set m_from = new HashSet();

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

        for (Iterator it = m_from.iterator(); it.hasNext(); ) {
            Integer idx = (Integer) it.next();
            List from = getPartition(idx);
            for (int i = 0; i < from.size(); i++) {
                Object o = from.get(i);
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
