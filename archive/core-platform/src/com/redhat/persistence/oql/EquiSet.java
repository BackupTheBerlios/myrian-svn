package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * EquiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/23 $
 **/

class EquiSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/EquiSet.java#2 $ by $Author: dennis $, $DateTime: 2004/03/23 03:39:40 $";

    private static final Logger s_log = Logger.getLogger(EquiSet.class);

    private Generator m_generator;

    private Map m_nodes = new HashMap();
    private Set m_sets = new HashSet();

    private EquiSet m_equiframes = null;

    private Set m_frames = new HashSet();
    private List m_framesets = null;

    EquiSet(Generator generator) {
        m_generator = generator;
    }

    Set getSets() {
        return m_sets;
    }

    Set get(QValue a) {
        return (Set) m_nodes.get(a);
    }

    boolean equate(QValue a, QValue b) {
        m_frames.add(a.getFrame());
        m_frames.add(b.getFrame());
        Set s = new HashSet();
        s.add(a);
        s.add(b);
        return add(s);
    }

    void collapse() {
        while (collapse(m_frames)) {};
    }

    boolean collapse(Collection frames) {
        MultiMap result = new MultiMap();
        MultiMap columns = new MultiMap();
        for (Iterator it = frames.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            List keys = keys(frame);
            if (keys == null) { continue; }
            for (int i = 0; i < keys.size(); i++) {
                Object key = keys.get(i);
                result.add(key, frame);
                columns.addAll(key, frame.getColumns());
            }
        }

        boolean modified = false;

        for (Iterator it = result.keys().iterator(); it.hasNext(); ) {
            Object key = (Object) it.next();
            Set set = result.get(key);
            Set cols = columns.get(key);
            for (Iterator iter = cols.iterator(); iter.hasNext(); ) {
                String col = (String) iter.next();
                Set eq = new HashSet();
                for (Iterator ii = set.iterator(); ii.hasNext(); ) {
                    QFrame frame = (QFrame) ii.next();
                    eq.add(frame.getValue(col));
                }
                modified |= add(eq);
            }
        }

        if (!modified) {
            m_framesets = new ArrayList(result.keys().size());
            for (Iterator it = result.keys().iterator(); it.hasNext(); ) {
                m_framesets.add(result.get(it.next()));
            }
        }

        return modified;
    }

    List keys(QFrame frame) {
        Table t = m_generator.getRoot().getTable(frame.getTable());
        if (t == null) { return null; }
        List result = null;
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
                Set set = get(v);
                if (set == null) {
                    continue OUTER;
                }
                if (key == null) {
                    key = new Pointer(set);
                } else {
                    key = new CompoundKey(new Pointer(set), key);
                }
            }
            if (result == null) { result = new ArrayList(); }
            result.add(key);
        }
        return result;
    }

    List getFrameSets() {
        return m_framesets;
    }

    boolean add(Set set) {
        Set to = null;
        Set from = new HashSet();

        for (Iterator it = set.iterator(); it.hasNext(); ) {
            Object o = it.next();
            Set s = (Set) m_nodes.get(o);
            if (s == null) {
                from.add(new Pointer(set));
            } else if (to == null) {
                to = s;
            } else if (to == s) {
                // do nothing
            } else if (s.size() > to.size()) {
                from.add(new Pointer(to));
                to = s;
            } else {
                from.add(new Pointer(s));
            }
        }

        if (to == null && !from.isEmpty()) {
            to = new HashSet();
        }

        boolean modified = false;
        for (Iterator it = from.iterator(); it.hasNext(); ) {
            Pointer p = (Pointer) it.next();
            m_sets.remove(p);
            for (Iterator iter = p.set.iterator(); iter.hasNext(); ) {
                Object o = iter.next();
                modified |= to.add(o);
                m_nodes.put(o, to);
            }
        }
        if (to != null) {
            m_sets.add(new Pointer(to));
        }

        return modified;
    }

    boolean addAll(EquiSet equiset) {
        boolean modified = false;
        for (Iterator it = equiset.m_sets.iterator(); it.hasNext(); ) {
            Pointer p = (Pointer) it.next();
            modified |= add(p.set);
        }
        m_frames.addAll(equiset.m_frames);
        return modified;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (Iterator iter = m_sets.iterator(); iter.hasNext(); ) {
            Pointer p = (Pointer) iter.next();
            for (Iterator it = p.set.iterator(); it.hasNext(); ) {
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append(", ");
                }
            }
            if (iter.hasNext()) {
                buf.append(" | ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    private static class Pointer {

        Set set;

        Pointer(Set set) {
            this.set = set;
        }

        public int hashCode() {
            return System.identityHashCode(set);
        }

        public boolean equals(Object o) {
            Pointer p = (Pointer) o;
            return set == p.set;
        }

    }

}
