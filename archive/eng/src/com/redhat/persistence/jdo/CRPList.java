package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Static;
import com.redhat.persistence.oql.Expression;

import java.util.*;
import javax.jdo.JDOHelper;

/**
 * CRPList
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/07/08 $
 **/

class CRPList extends CRPCollection implements List {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/CRPList.java#5 $ by $Author: vadim $, $DateTime: 2004/07/08 17:05:37 $";

    private final static String INDEX = "index";

    transient private Session m_ssn;
    transient private Object m_object;
    transient private Property m_property;
    transient private Property m_container;
    transient private Property m_index;
    transient private Property m_element;

    CRPList() {}

    CRPList(Session ssn, Object object, Property property) {
        m_ssn = ssn;
        m_object = object;
        m_property = property;
    }

    private void init() {
        ObjectType type = m_property.getType();
        List keys = type.getKeyProperties();
        if (keys.size() != 2) {
            throw new IllegalStateException
                ("cannot map from " + type + " to java list element");
        }
        m_container = null;
        m_index = null;
        m_element = null;
        Collection properties = type.getProperties();
        for (Iterator it = properties.iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            if (p.isKeyProperty()) {
                if (INDEX.equals(p.getName())) {
                    Class indexClass = p.getType().getJavaClass();
                    if (Number.class.isAssignableFrom(indexClass)) {
                            m_index = p;
                    } else {
                        throw new IllegalStateException
                            (p + " is not of an integral type: " +
                             indexClass.getName());

                    }
                } else {
                    if (m_container == null) {
                        m_container = p;
                    } else {
                        throw new IllegalStateException
                            ("ambiguous container reference for " + type);
                    }
                }
            } else if (!p.isKeyProperty()) {
                if (m_element == null) {
                    m_element = p;
                } else {
                    throw new IllegalStateException
                        ("ambiguous element property for " + type);
                }
            }
        }
    }

    Session ssn() {
        if (m_ssn == null) {
            PersistenceManagerImpl pmi = ((PersistenceManagerImpl) JDOHelper
                                          .getPersistenceManager(this));
            m_ssn = pmi.getSession();
            StateManagerImpl smi = pmi.getStateManager(this);
            PropertyMap pmap = smi.getPropertyMap();
            m_object = m_ssn.retrieve(pmap);
            ObjectType type = pmap.getObjectType();
            String name = smi.getPrefix() + "elements";
            m_property = type.getProperty(name);
            if (m_property == null) {
                throw new IllegalStateException("no " + name + " in " + type);
            }
            init();
        }

        return m_ssn;
    }

    ObjectType type() {
        return m_element.getType();
    }

    Expression expression() {
        return new Get(elements(), m_element.getName());
    }

    Expression elements() {
        return new Get(new Literal(m_object), m_property.getName());
    }

    private void lock() {
        C.lock(ssn(), m_object);
    }

    public void clear() {
        ssn().clear(m_object, m_property);
    }

    private ListElement create(int index, Object object) {
        PropertyMap pmap = new PropertyMap(m_property.getType());
        pmap.put(m_index, new Integer(index));
        pmap.put(m_container, m_object);
        Adapter ad = ssn().getRoot().getAdapter(ListElement.class);
        ListElement element = (ListElement) ad.getObject
            (pmap.getObjectType().getBasetype(), pmap, ssn());
        ssn().create(element);
        ssn().set(element, m_element, object);
        return element;
    }

    public boolean add(Object obj) {
        lock();
        int size = size();
        create(size, obj);
        return true;
    }

    public List subList(int from, int to) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        return listIterator();
    }

    public ListIterator listIterator() {
        return listIterator(0);
    }

    public ListIterator listIterator(int from) {
        return new CRPListIterator(from);
    }

    private int indexOf(Object obj, boolean first) {
        Expression expr = elements();
        expr = new Filter
            (expr, new Equals
             (new Variable(m_element.getName()), new Literal(obj)));
        expr = new Sort(expr, new Variable(m_index.getName()),
                        first ? Sort.ASCENDING : Sort.DESCENDING);
        expr = new Limit(expr, new Literal(new Integer(1)));
        Cursor c = C.cursor(ssn(), m_property.getType(), expr);
        try {
            if (c.next()) {
                ListElement el = (ListElement) c.get();
                return el.getIndex().intValue();
            } else {
                return -1;
            }
        } finally {
            c.close();
        }
    }

    public int lastIndexOf(Object obj) {
        return indexOf(obj, false);
    }

    public int indexOf(Object obj) {
        return indexOf(obj, true);
    }

    private int check(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("negative index: " + index);
        }
        lock();
        int size = size();
        if (index >= size) {
            throw new IndexOutOfBoundsException
                (index + " too large for list of size " + size);
        }
        return size;
    }

    public Object remove(int index) {
        lock();
        Object result = get(index);
        for (int size = size(); index < size - 1; index++) {
            set(index, get(index + 1));
        }
        ssn().delete(getElement(index));
        return result;
    }

    public boolean remove(Object o) {
        lock();
        int idx = indexOf(o);
        if (idx < 0) {
            return false;
        } else {
            remove(idx);
            return true;
        }
    }

    public void add(int index, Object obj) {
        lock();
        int size = size();
        while (index < size) {
            obj = set(index++, obj);
        }
        // XXX: can't use add(obj) here because right now null
        // elements at the end of the list don't get counted since
        // there are no rows in the db for them.
        create(size, obj);
    }

    public Object set(int index, Object obj) {
        int size = check(index);
        ListElement el = getElement(index);
        if (el == null) {
            if (obj != null) {
                create(index, obj);
            }
            return null;
        } else {
            Object result = ssn().get(el, m_element);
            if (obj == null && el.getIndex().intValue() < size - 1) {
                ssn().delete(el);
            } else {
                ssn().set(el, m_element, obj);
            }
            return result;
        }
    }

    private ListElement getElement(int index) {
        Expression expr = elements();
        expr = new Filter(expr, new Equals(new Variable(m_index.getName()),
                                           new Literal(new Integer(index))));
        Cursor c = C.cursor(ssn(), m_property.getType(), expr);
        try {
            if (c.next()) {
                return (ListElement) c.get();
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }

    public Object get(int index) {
        ListElement el = getElement(index);
        if (el == null) {
            return null;
        } else {
            return ssn().get(el, m_element);
        }
    }

    public boolean addAll(int index, Collection c) {
        boolean modified = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            add(index, it.next());
            modified = true;
        }
        return modified;
    }

    private static final String MAX = "max";

    public int size() {
        // XXX: fix this
        ObjectType integer = ssn().getRoot().getObjectType("global.Integer");
        if (integer == null) {
            integer = ssn().getRoot().getObjectType("Integer");
        }
        Signature sig = new Signature() {
            public Query makeQuery(Expression e) {
                Query q = new Query(e);
                q.fetch
                    (MAX, new Static("RAW[max](" + m_index.getName() + ")"));
                return q;
            }
            public String getColumn(Path p) {
                return MAX;
            }
            public boolean isFetched(Path p) {
                return true;
            }
        };
        sig.addSource(integer, Path.get(MAX));
        sig.addPath(MAX);
        DataSet ds = new DataSet(ssn(), sig, elements());
        Cursor c = ds.getCursor();
        try {
            if (c.next()) {
                Integer size = (Integer) c.get(MAX);
                if (size == null) {
                    return 0;
                } else {
                    return size.intValue() + 1;
                }
            } else {
                throw new IllegalStateException
                    ("size query returned no rows");
            }
        } finally {
            c.close();
        }
    }

    public boolean equals(Object o) {
        if (o instanceof List) {
            List l = (List) o;
            int s1 = size();
            int s2 = l.size();
            if (s1 != s2) { return false; }
            Iterator it1 = iterator();
            Iterator it2 = iterator();
            while (it1.hasNext()) {
                Object o1 = it1.next();
                if (it2.hasNext()) {
                    Object o2 = it2.next();
                    if (!o1.equals(o2)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (it2.hasNext()) {
                return false;
            }
            return true;
        } else {
            return super.equals(o);
        }
    }

    private static final int FORWARD = 0;
    private static final int REVERSE = 1;

    private class CRPListIterator implements ListIterator {

        private int m_index;
        private ListElement m_current;
        private ListElement m_next;
        private Cursor m_cursor = null;
        private int m_direction = FORWARD;

        CRPListIterator(int from) {
            m_index = from;
        }

        private void advance(int direction) {
            if (m_cursor == null || m_direction != direction) {
                if (m_direction != direction) {
                    if (direction == FORWARD) {
                        m_index++;
                    } else {
                        m_index--;
                    }
                }

                // XXX
                if (CRPList.this.m_index == null) {
                    ssn();
                }

                String idx = CRPList.this.m_index.getName();
                Expression expr = new Sort
                    (new Filter
                     (elements(), new Static
                      (idx + (direction == FORWARD ? " >= " : " <= ") +
                       ":index", Collections.singletonMap
                       (INDEX, new Integer(m_index)))),
                     new Variable(idx),
                     direction == FORWARD ? Sort.ASCENDING : Sort.DESCENDING);
                m_cursor = C.cursor(ssn(), m_property.getType(), expr);
                m_direction = direction;
            }

            if (m_cursor.next()) {
                m_next = (ListElement) m_cursor.get();
            } else {
                m_next = null;
            }
        }

        private void start(int direction) {
            if (m_cursor == null || m_direction != direction) {
                advance(direction);
            }
        }

        private boolean hasNext(int direction) {
            start(direction);
            return m_next != null;
        }

        private Object next(int direction) {
            start(direction);
            if (m_index == m_next.getIndex().intValue()) {
                m_current = m_next;
                advance(direction);
            } else {
                m_current = null;
            }

            if (direction == FORWARD) {
                m_index++;
            } else {
                m_index--;
            }

            if (m_current == null) {
                return null;
            } else {
                return ssn().get(m_current, m_element);
            }
        }

        private int nextIndex(int direction) {
            if (m_direction == direction) {
                return m_index;
            } else {
                return direction == FORWARD ? m_index + 1 : m_index - 1;
            }
        }

        public boolean hasNext() {
            return hasNext(FORWARD);
        }

        public int nextIndex() {
            return nextIndex(FORWARD);
        }

        public Object next() {
            return next(FORWARD);
        }

        public boolean hasPrevious() {
            return hasNext(REVERSE);
        }

        public int previousIndex() {
            return nextIndex(REVERSE);
        }

        public Object previous() {
            return next(REVERSE);
        }

        public void add(Object obj) {
            throw new UnsupportedOperationException();
        }

        public void set(Object obj) {
            throw new UnsupportedOperationException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
