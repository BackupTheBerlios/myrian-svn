package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.common.Path;
import com.arsdigita.persistence.proto.PersistentCollection;
import com.arsdigita.persistence.proto.DataSet;
import com.arsdigita.persistence.proto.Cursor;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.Signature;
import com.arsdigita.persistence.proto.Parameter;
import com.arsdigita.persistence.proto.PassthroughFilter;

import com.arsdigita.util.StringUtils;

import java.util.*;

/**
 * DataQueryImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #14 $ $Date: 2003/03/28 $
 **/

class DataQueryImpl implements DataQuery {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataQueryImpl.java#14 $ by $Author: rhs $, $DateTime: 2003/03/28 17:56:58 $";

    private static final FilterFactory FACTORY = new FilterFactoryImpl();

    private Session m_ssn;
    private com.arsdigita.persistence.proto.Session m_pssn;
    private HashMap m_bindings = new HashMap();
    private Query m_query;
    PersistentCollection m_pc;
    Cursor m_cursor = null;
    private CompoundFilter m_filter = getFilterFactory().and();

    // This indicates the limits on the number of rows returned by the query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    DataQueryImpl(Session ssn, PersistentCollection pc) {
        m_ssn = ssn;
        m_pssn = ssn.getProtoSession();
        m_pc = pc;
	m_query = new Query(m_pc.getDataSet().getQuery(), null);
    }

    Session getSession() {
        return m_ssn;
    }

    public CompoundType getType() {
        throw new Error("not implemented");
    }

    public boolean hasProperty(String propertyName) {
        throw new Error("not implemented");
    }

    public void reset() {
        throw new Error("not implemented");
    }


    public boolean first() {
        throw new Error("not implemented");
    }

    public boolean isEmpty() {
        return m_pssn.retrieve(makeQuery()).getDataSet().isEmpty();
    }


    public boolean isBeforeFirst() {
        checkCursor();
        return m_cursor.isBeforeFirst();
    }

    public boolean isFirst() {
        checkCursor();
        return m_cursor.isFirst();
    }


    public boolean isLast() {
        throw new Error("not implemented");
    }


    public boolean isAfterLast() {
        checkCursor();
        return m_cursor.isAfterLast();
    }


    public boolean last() {
        throw new Error("not implemented");
    }


    public boolean previous() {
        throw new Error("not implemented");
    }


    public Filter setFilter(String conditions) {
        clearFilter();
        return addFilter(conditions);
    }


    public Filter addFilter(String conditions) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("The filter cannot be set on an active data query. " +
                 "Data query must be rewound.");
        }

        return m_filter.addFilter(conditions);
    }


    public Filter addFilter(Filter filter) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("The filter cannot be set on an active data query. " +
                 "Data query must be rewound.");
        }

        return m_filter.addFilter(filter);
    }

    public boolean removeFilter(Filter filter) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("The filter cannot be removed on an active data query. " +
                 "Data query must be rewound.");
        }

        return m_filter.removeFilter(filter);
    }

    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return addFilter(getFilterFactory().in(propertyName, subqueryName));
    }


    public Filter addInSubqueryFilter(String propertyName,
                                      String subQueryProperty,
                                      String queryName) {
        return addFilter
            (getFilterFactory().in
             (propertyName, subQueryProperty, queryName));
    }

    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        return addFilter(getFilterFactory().notIn(propertyName, subqueryName));
    }

    public Filter addEqualsFilter(String attribute, Object value) {
        return addFilter(getFilterFactory().equals(attribute, value));
    }


    public Filter addNotEqualsFilter(String attribute, Object value) {
        return addFilter(getFilterFactory().notEquals(attribute, value));
    }


    public void clearFilter() {
        if (m_cursor != null) {
            throw new PersistenceException
                ("Cannot clear the filter on an active data query. " +
                 "Data query must be rewound.");
        }
        m_filter = getFilterFactory().and();
    }

    public FilterFactory getFilterFactory() {
        return FACTORY;
    }


    public void setOrder(String order) {
        m_query.clearOrder();
        addOrder(order);
    }


    public void addOrder(String order) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("Cannot order an active data query. " +
                 "Data query must be rewound.");
        }
        String[] orders = StringUtils.split(order, ',');
        for (int i = 0; i < orders.length; i++) {
            String[] parts = StringUtils.split(orders[i].trim(), ' ');
            boolean isAscending;
            if (parts.length == 1) {
                isAscending = true;
            } else if (parts.length == 2) {
                isAscending = parts[1].startsWith("asc");
            } else {
                throw new IllegalArgumentException
                    ("bad order: " + order);
            }

            m_query.addOrder(Path.get(parts[0]), isAscending);
        }
    }


    public void addOrderWithNull(String orderOne, Object orderTwo,
                          boolean isAscending) {
        throw new Error("not implemented");
    }


    public void clearOrder() {
        throw new Error("not implemented");
    }


    private void setParameter(Query query, String parameterName,
			      Object value) {
        if (value == null) {
            return;
        }

        Object tobj;
        if (value instanceof Collection) {
            Collection c = (Collection) value;
            if (c.size() == 0) {
                throw new Error("zero sized collection");
            } else {
                tobj = c.iterator().next();
            }
        } else {
            tobj = value;
        }

        Signature sig = query.getSignature();
        Path path = Path.get(parameterName);
        Parameter p = sig.getParameter(path);
        if (p == null) {
            // XXX: should add notion of multiplicity to Parameter
            p = new Parameter(m_pssn.getObjectType(tobj), path);
            sig.addParameter(p);
        }
        query.set(p, value);
    }


    public void setParameter(String parameterName, Object value) {
	m_bindings.put(parameterName, value);
    }


    public Object getParameter(String parameterName) {
	return m_bindings.get(parameterName);
    }


    public boolean isNoView() {
        throw new Error("not implemented");
    }

    public void setNoView(boolean isNoView) {
        throw new Error("not implemented");
    }


    public void setRange(Integer beginIndex) {
        setRange(beginIndex, null);
    }

    public void setRange(Integer beginIndex, Integer endIndex) {
        if (endIndex != null && endIndex.compareTo(beginIndex) <= 0) {
            throw new PersistenceException
                ("The beginIndex [" + beginIndex + "] must be strictly less " +
                 "than the endIndex [" + endIndex + "]");
        }

        m_query.setOffset(new Integer(beginIndex.intValue() - 1));
        if (endIndex != null) {
            m_query.setLimit(new Integer(endIndex.intValue() -
					 beginIndex.intValue()));
        }
    }


    public Map getPropertyValues() {
        throw new Error("not implemented");
    }


    public void setReturnsUpperBound(int upperBound) {
        m_upperBound = upperBound;
    }


    public void setReturnsLowerBound(int lowerBound) {
        if (lowerBound > 1 || lowerBound < 0) {
            throw new PersistenceException
                ("The lower bound for a given query must be 0 or 1.");
        }
        m_lowerBound = lowerBound;
    }

    public void alias(String fromPrefix, String toPrefix) {
        throw new Error("not implemented");
    }

    public void close() {
        if (m_cursor != null) {
            m_cursor.close();
        }
    }

    public void rewind() {
        if (m_cursor != null) {
            m_cursor.rewind();
        }
    }


    public Object get(String propertyName) {
        return m_cursor.get(propertyName);
    }


    public int getPosition() {
        checkCursor();
        return (int) m_cursor.getPosition();
    }

    private Query makeQuery() {
        String conditions = m_filter.getConditions();
	Query q;
        if (conditions == null || conditions.equals("")) {
	    q = new Query(m_query, null);
	} else {
	    q = new Query(m_query, new PassthroughFilter(conditions));
	}

	m_bindings.putAll(m_filter.getBindings());
	for (Iterator it = m_bindings.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry me = (Map.Entry) it.next();
	    String key = (String) me.getKey();
	    setParameter(q, key, me.getValue());
	}

        return q;
    }

    private void checkCursor() {
        if (m_cursor == null) {
            m_cursor = m_pssn.retrieve(makeQuery()).getDataSet().getCursor();
        }
    }

    public boolean next() {
        checkCursor();
        int pre = getPosition();
        boolean result = m_cursor.next();
        if (result) {
            if (getPosition() == m_upperBound) {
                if (m_cursor.next()) {
                    throw new PersistenceException
                        ("cursor exceeded upper bound");
                }
            }
        } else {
            if (pre < m_lowerBound) {
                throw new PersistenceException
                    ("cursor failed to meet lower bound");
            }
        }

        return result;
    }

    public long size() {
        return m_pssn.retrieve(makeQuery()).getDataSet().size();
    }

}
