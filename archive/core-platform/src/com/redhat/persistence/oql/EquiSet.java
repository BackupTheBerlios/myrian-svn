package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * EquiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

class EquiSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/EquiSet.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    private static final Logger s_log = Logger.getLogger(EquiSet.class);

    private Generator m_generator;
    private List m_sets = new ArrayList();
    private List m_framesets = null;

    EquiSet(Generator generator) {
        m_generator = generator;
    }

    List getSets() {
        return m_sets;
    }

    Set get(QValue a) {
        int index = indexOf(a);
        if (index < 0) {
            return null;
        } else {
            return (Set) m_sets.get(index);
        }
    }

    int indexOf(QValue a) {
        for (int i = 0; i < m_sets.size(); i++) {
            Set set = (Set) m_sets.get(i);
            if (set.contains(a)) { return i; }
        }
        return -1;
    }

    boolean equate(QValue a, QValue b) {
        int aindex = -1;
        int bindex = -1;
        Set aset = null;
        Set bset = null;

        for (int i = 0; i < m_sets.size(); i++) {
            Set set = (Set) m_sets.get(i);
            if (set.contains(a)) {
                if (aset != null) {
                    throw new IllegalStateException
                        ("not a partition: " + set + ", " + aset);
                }
                aindex = i;
                aset = set;
            }
            if (set.contains(b)) {
                if (bset != null) {
                    throw new IllegalStateException
                        ("not a partition: " + set + ", " + bset);
                }
                bindex = i;
                bset = set;
            }
        }

        if (aset != null && bset != null) {
            if (aindex == bindex) {
                return false;
            } else {
                aset.addAll(bset);
                m_sets.remove(bindex);
            }
        } else if (aset != null) {
            aset.add(b);
        } else if (bset != null) {
            bset.add(a);
        } else {
            aset = new HashSet();
            aset.add(a);
            aset.add(b);
            m_sets.add(aset);
        }

        return true;
    }

    void collapse() {
        Set frames = new HashSet();
        for (int i = 0; i < m_sets.size(); i++) {
            Set set = (Set) m_sets.get(i);
            for (Iterator it = set.iterator(); it.hasNext(); ) {
                QValue v = (QValue) it.next();
                frames.add(v.getFrame());
            }
        }
        while (collapse(frames)) {};
    }

    boolean collapse(Collection frames) {
        MultiMap result = new MultiMap();
        MultiMap columns = new MultiMap();
        for (Iterator it = frames.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            Table t = m_generator.getRoot().getTable(frame.getTable());
            if (t == null) { continue; }
            MIDDLE: for (Iterator iter = t.getConstraints().iterator();
                         iter.hasNext(); ) {
                Constraint c = (Constraint) iter.next();
                if (!(c instanceof UniqueKey)) { continue; }
                UniqueKey uk = (UniqueKey) c;
                Column[] cols = uk.getColumns();
                Object key = uk;
                for (int i = 0; i < cols.length; i++) {
                    if (!frame.hasValue(cols[i].getName())) {
                        continue MIDDLE;
                    }
                    QValue v = frame.getValue(cols[i].getName());
                    Integer vindex = new Integer(indexOf(v));
                    if (vindex.intValue() < 0) {
                        continue MIDDLE;
                    }
                    if (key == null) {
                        key = vindex;
                    } else {
                        key = new CompoundKey(vindex, key);
                    }
                }
                result.add(key, frame);
                columns.addAll(key, frame.getColumns());
            }
        }

        boolean modified = false;

        for (Iterator it = result.keys().iterator(); it.hasNext(); ) {
            Object key = (Object) it.next();
            Set set = result.get(key);
            Set cols = columns.get(key);
            QFrame from = null;
            for (Iterator iter = set.iterator(); iter.hasNext(); ) {
                QFrame to = (QFrame) iter.next();
                if (from != null) {
                    for (Iterator ii = cols.iterator(); ii.hasNext(); ) {
                        String col = (String) ii.next();
                        QValue v1 = from.getValue(col);
                        QValue v2 = to.getValue(col);
                        modified |= equate(v1, v2);
                    }
                }
                from = to;
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

    List getFrameSets() {
        return m_framesets;
    }

}
