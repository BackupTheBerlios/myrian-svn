/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.ProtoException;
import com.redhat.persistence.PersistentCollection;
import com.redhat.persistence.Cursor;
import com.redhat.persistence.Query;
import com.redhat.persistence.Signature;
import com.redhat.persistence.Parameter;
import com.redhat.persistence.Expression;
import com.redhat.persistence.metadata.Root;
import com.arsdigita.util.Assert;

import java.io.StringReader;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * DataQueryImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/10/01 $
 **/

class DataQueryImpl implements DataQuery {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DataQueryImpl.java#4 $ by $Author: rhs $, $DateTime: 2003/10/01 14:17:42 $";

    private static final Logger s_log = Logger.getLogger(DataQueryImpl.class);

    private Session m_ssn;
    private com.redhat.persistence.Session m_pssn;
    private HashMap m_bindings = new HashMap();
    final private Query m_original;
    private Query m_query;
    Cursor m_cursor = null;
    private CompoundFilter m_filter;

    // This indicates the limits on the number of rows returned by the query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    private final List m_aliases = new ArrayList();
    private final FilterFactory m_factory;

    DataQueryImpl(Session ssn, PersistentCollection pc) {
	this(ssn, pc.getDataSet().getQuery());
    }

    DataQueryImpl(Session ssn, Query query) {
        m_ssn = ssn;
        m_pssn = ssn.getProtoSession();
	m_original = query;
	m_query = new Query(m_original, null);
        m_factory = new FilterFactoryImpl(ssn);
        m_filter = getFilterFactory().and();
    }

    Session getSession() {
        return m_ssn;
    }

    Query getOriginal() {
	return m_original;
    }

    public CompoundType getType() {
        throw new Error("not implemented");
    }

    public boolean hasProperty(String propertyName) {
        return propertyExists(unalias(Path.get(propertyName)));
    }

    public void reset() {
	close();
	m_cursor = null;
        m_bindings.clear();
	m_query = new Query(m_original, null);
	m_filter = getFilterFactory().and();
	m_lowerBound = 0;
	m_upperBound = Integer.MAX_VALUE;
    }


    public boolean first() {
        throw new Error("not implemented");
    }

    public boolean isEmpty() {
	try {
	    return m_pssn.retrieve(makeQuery()).getDataSet().isEmpty();
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
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


    public void addPath(String path) {
        if (m_cursor != null) {
            throw new PersistenceException
                ("Paths cannot be added on an active data query.");
        }

        m_query.getSignature().addPath(Path.get(path));
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
        return m_factory;
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
        order = unalias(order);
        m_query.addOrder(Expression.passthrough(order), true);
    }

    private int m_order = 0;

    public void addOrderWithNull(String orderOne, Object orderTwo,
				 boolean isAscending) {
        String suffix = null;
        if (isAscending) {
            suffix = "asc";
        } else {
            suffix = "desc";
        }

        Object secondElement = orderTwo;
        if (orderTwo instanceof String && orderTwo != null) {
            Path path = Path.get((String) orderTwo);
            if (!m_query.getSignature().exists(path)) {
                String var = "order" + m_order++;
                secondElement = ":" + var;
                setParameter(var, orderTwo);
                if (orderOne != null) {
                    Root root = getSession().getRoot();
                    if (!root.getObjectType("global.String").equals
                        (m_query.getSignature().getType
                         (Path.get(orderOne)))) {
                        // this means that there is going to be a type conflict
                        // by the DB so we prevent it here
                        throw new PersistenceException("type mismatch");
                    }
                }
            }
        }

        addOrder("case when (" + orderOne + " is null) then " +
                 secondElement + " else " + orderOne + " end " + suffix);
    }

    public void clearOrder() {
        m_query.clearOrder();
        m_order = 0;
    }

    private void setParameter(Query query, String parameterName,
			      Object value) {
        Object tobj;
	if (value == null) {
	    tobj = "";
	} else if (value instanceof Collection) {
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
	m_bindings.put(":" + parameterName, value);
    }


    public Object getParameter(String parameterName) {
	return m_bindings.get(":" + parameterName);
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
        m_aliases.add(new Alias(fromPrefix, toPrefix));
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
        Path path = unalias(Path.get(propertyName));
	try {
	    return m_cursor.get(path);
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
    }


    public int getPosition() {
        checkCursor();
        return (int) m_cursor.getPosition();
    }

    Query makeQuery() {
        String conditions = m_filter.getConditions();
	Query q;
        if (conditions == null || conditions.equals("")) {
	    q = new Query(m_query, null);
	} else {
            conditions = unalias(conditions);
	    q = new Query(m_query, Expression.passthrough(conditions));
	}

	Map filterBindings = m_filter.getBindings();
	for (Iterator it = filterBindings.entrySet().iterator();
	     it.hasNext(); ) {
	    Map.Entry me = (Map.Entry) it.next();
	    String key = (String) me.getKey();
            if (key.charAt(0) == ':') {
                key = unalias(Path.get(key)).toString();
            }
	    setParameter(key, me.getValue());
	}

	for (Iterator it = m_bindings.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry me = (Map.Entry) it.next();
	    String key = (String) me.getKey();
            if (key.charAt(0) == ':') {
                key = unalias(Path.get(key)).toString();
            }
	    setParameter(q, key, me.getValue());
	}

        return q;
    }

    private void checkCursor() {
        if (m_cursor == null) {
	    try {
		m_cursor = execute(makeQuery());
	    } catch (ProtoException e) {
		throw PersistenceException.newInstance(e);
	    }
        }
    }

    protected Cursor execute(Query query) {
	return m_pssn.retrieve(query).getDataSet().getCursor();
    }

    public boolean next() {
        checkCursor();
	if (m_cursor.isClosed()) {
	    return false;
	}

        int pre = getPosition();

	boolean result;
	try {
	    result = m_cursor.next();
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}

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
	try {
	    return m_pssn.retrieve(makeQuery()).getDataSet().size();
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
    }

    private final boolean propertyExists(Path path) {
        return m_query.getSignature().exists(path);
    }

    private String unalias(String expr) {
        SQLParser p = new SQLParser
            (new StringReader(expr),
             new SQLParser.IdentityMapper() {
                 public Path map(Path path) {
                     return unalias(path);
                 }
             });

        try {
            p.sql();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return p.getSQL().toString();
    }

    private Path unalias(Path path) {
	if (s_log.isDebugEnabled()) {
	    s_log.debug("External Path: " + path);
	    s_log.debug("Aliases: " + m_aliases.toString());
	}

        Path result = path;

        for (Iterator it = m_aliases.iterator(); it.hasNext(); ) {
            Alias alias = (Alias) it.next();
            if (alias.isMatch(path)) {
		if (s_log.isDebugEnabled()) {
		    s_log.debug("matched " + alias);
		}
                Path candidate = alias.unalias(path);
                if (propertyExists(candidate)) {
                    result = candidate;
                    break;
                }

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Candidate " + candidate + " doesn't exist.");
                }
            } else {
		if (s_log.isDebugEnabled()) {
		    s_log.debug("didn't match " + alias);
		}
            }
        }

	if (s_log.isDebugEnabled()) {
	    s_log.debug("Internal Path: " + result);
	}

        return result;
    }

    private static class Alias {

        private Path m_from;
        private Path m_to;

        public Alias(String from, String to) {
            Assert.assertNotEmpty(from, "from");
            Assert.assertNotEmpty(to, "to");

            m_from = Path.get(from);
            m_to = Path.get(to);
        }

        private static final boolean isWildcard(Path path) {
            return path.getParent() == null && path.getName().equals("*");
        }

        public boolean isMatch(Path path) {
            if (isWildcard(m_from)) { return true; }
            if (m_from.getParent() == null) { return m_from.equals(path); }
            while (path.getParent() != null) {
                path = path.getParent();
            }
            return m_from.getParent().equals(path);
        }

        public Path unalias(Path path) {
            if (isWildcard(m_from) && isWildcard(m_to)) {
                return path;
            } else if (isWildcard(m_from) && !isWildcard(m_to)) {
                if (m_to.getParent() != null) {
                    return Path.add(m_to.getParent(), path);
                } else {
                    throw new IllegalStateException(this + " " + path);
                }
            } else if (!isWildcard(m_from) && isWildcard(m_to)) {
                return path.getRelative(m_from);
            } else {
                try {
                    return Path.add(m_to, path.getRelative(m_from));
                } catch (RuntimeException e) {
                    throw new PersistenceException(this + " " + path, e);
                }
            }
        }

        public String toString() {
            return m_from + " --> " + m_to;
        }

    }

}
