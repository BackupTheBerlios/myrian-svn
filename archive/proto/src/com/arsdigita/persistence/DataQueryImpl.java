package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.common.Path;
import com.arsdigita.persistence.proto.PersistentCollection;
import com.arsdigita.persistence.proto.DataSet;
import com.arsdigita.persistence.proto.Cursor;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.Signature;
import com.arsdigita.persistence.proto.PassthroughFilter;

import com.arsdigita.util.StringUtils;

import java.util.*;

/**
 * DataQueryImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2003/03/01 $
 **/

class DataQueryImpl implements DataQuery {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataQueryImpl.java#10 $ by $Author: rhs $, $DateTime: 2003/03/01 02:23:27 $";

    private static final FilterFactory FACTORY = new FilterFactoryImpl();

    private Session m_ssn;
    private com.arsdigita.persistence.proto.Session m_pssn;
    PersistentCollection m_pc;
    Cursor m_cursor = null;
    private CompoundFilter m_filter = getFilterFactory().and();

    DataQueryImpl(Session ssn, PersistentCollection pc) {
        m_ssn = ssn;
        m_pssn = ssn.getProtoSession();
        m_pc = pc;
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
        return m_pc.getDataSet().isEmpty(makeFilter());
    }


    public boolean isBeforeFirst() {
        throw new Error("not implemented");
    }

    public boolean isFirst() {
        return m_cursor.isFirst();
    }


    public boolean isLast() {
        throw new Error("not implemented");
    }


    public boolean isAfterLast() {
        throw new Error("not implemented");
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
        Query q = m_pc.getDataSet().getQuery();
        q.clearOrder();
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

            m_pc.getDataSet().getQuery().addOrder(Path.get(parts[0]),
                                                  isAscending);
        }
    }


    public void addOrderWithNull(String orderOne, Object orderTwo,
                          boolean isAscending) {
        throw new Error("not implemented");
    }


    public void clearOrder() {
        throw new Error("not implemented");
    }


    public void setParameter(String parameterName, Object value) {
        throw new Error("not implemented");
    }


    public Object getParameter(String parameterName) {
        throw new Error("not implemented");
    }


    public boolean isNoView() {
        throw new Error("not implemented");
    }

    public void setNoView(boolean isNoView) {
        throw new Error("not implemented");
    }


    public void setRange(Integer beginIndex) {
        throw new Error("not implemented");
    }

    public void setRange(Integer beginIndex, Integer endIndex) {
        throw new Error("not implemented");
    }


    public Map getPropertyValues() {
        throw new Error("not implemented");
    }


    public void setReturnsUpperBound(int upperBound) {
        throw new Error("not implemented");
    }


    public void setReturnsLowerBound(int lowerBound) {
        throw new Error("not implemented");
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
        m_cursor.rewind();
    }


    public Object get(String propertyName) {
        return m_cursor.get(propertyName);
    }


    public int getPosition() {
        return (int) m_cursor.getPosition();
    }

    private com.arsdigita.persistence.proto.Filter makeFilter() {
        String conditions = m_filter.getConditions();
        if (conditions == null || conditions.equals("")) {
            return null;
        }

        PassthroughFilter filter = new PassthroughFilter(conditions);
        Map bindings = m_filter.getBindings();
        for (Iterator it = bindings.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String key = (String) me.getKey();
            filter.setParameter(Path.get(key), me.getValue());
        }

        return filter;
    }

    public boolean next() {
        if (m_cursor == null) {
            m_cursor = m_pc.getDataSet().getCursor(makeFilter());
        }
        return m_cursor.next();
    }

    public long size() {
        return m_pc.getDataSet().size(makeFilter());
    }

}
