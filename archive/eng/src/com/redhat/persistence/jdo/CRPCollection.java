package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Expression;

import java.util.*;

/**
 * CRPCollection
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/04 $
 **/

abstract class CRPCollection implements Collection, OQLCollection {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/CRPCollection.java#4 $ by $Author: ashah $, $DateTime: 2004/08/04 17:36:12 $";

    CRPCollection() {}

    abstract Session ssn();

    abstract ObjectType type();

    protected Signature signature() {
        return new Signature(type());
    }

    private DataSet data() {
        return new DataSet(ssn(), signature(), expression());
    }

    public abstract void clear();

    public abstract boolean add(Object o);

    public abstract boolean remove(Object o);

    public Iterator iterator() {
        return new CRPIterator(data().getCursor());
    }

    public int size() {
        return (int) data().size();
    }

    public boolean isEmpty() {
        return data().isEmpty();
    }

    public boolean contains(Object o) {
        DataSet ds = new DataSet
            (ssn(), new Signature(), new Filter
             (new Define(expression(), "this"),
              new Equals(new Variable("this"), new Literal(o))));
        return !ds.isEmpty();
    }

    public boolean retainAll(Collection c) {
        boolean modified = false;
        for (Iterator it = iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (!c.contains(o)) {
                modified |= remove(o);
            }
        }
        return modified;
    }

    public boolean removeAll(Collection c) {
        boolean modified = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            modified |= remove(it.next());
        }
        return modified;
    }

    public boolean addAll(Collection c) {
        boolean modified = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            modified |= add(it.next());
        }
        return modified;
    }

    public boolean containsAll(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            if (!contains(it.next())) { return false; }
        }
        return true;
    }

    public Object[] toArray(Object[] in) {
        int size = size();
        Object[] result;
        if (size <= in.length) {
            result = in;
        } else {
            result = (Object[]) java.lang.reflect.Array.newInstance
                (in.getClass().getComponentType(), size);
        }
        int index = 0;
        for (Iterator it = iterator(); it.hasNext(); ) {
            result[index++] = it.next();
        }
        return result;
    }

    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    private class CRPIterator implements Iterator {

        private Cursor m_cursor;
        private Object m_current = null;
        private Object m_next = null;
        private boolean m_started = false;

        CRPIterator(Cursor cursor) {
            m_cursor = cursor;
        }

        private void advance() {
            if (m_cursor.next()) {
                m_next = m_cursor.get();
            } else {
                m_next = null;
            }
        }

        private void start() {
            if (!m_started) {
                advance();
                m_started = true;
            }
        }

        public boolean hasNext() {
            start();
            return m_next != null;
        }

        public Object next() {
            start();
            m_current = m_next;
            advance();
            return m_current;
        }

        public void remove() {
            CRPCollection.this.remove(m_current);
        }

    }

}
