package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.common.ParseException;
import com.arsdigita.persistence.proto.common.Path;
import com.arsdigita.persistence.proto.common.SQLParser;
import com.arsdigita.persistence.proto.engine.rdbms.UnboundParameterException;
import com.arsdigita.persistence.proto.PersistentCollection;
import com.arsdigita.persistence.proto.DataSet;
import com.arsdigita.persistence.proto.Cursor;
import com.arsdigita.persistence.proto.CursorException;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.Signature;
import com.arsdigita.persistence.proto.Parameter;
import com.arsdigita.persistence.proto.PassthroughFilter;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import java.io.StringReader;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * DataQueryImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #20 $ $Date: 2003/04/30 $
 **/

class DataQueryImpl implements DataQuery {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataQueryImpl.java#20 $ by $Author: ashah $, $DateTime: 2003/04/30 08:32:13 $";

    private static final Logger s_log = Logger.getLogger(DataQueryImpl.class);

    private static final FilterFactory s_factory = new FilterFactoryImpl();

    private Session m_ssn;
    private com.arsdigita.persistence.proto.Session m_pssn;
    private HashMap m_bindings = new HashMap();
    final private Query m_original;
    private Query m_query;
    Cursor m_cursor = null;
    private CompoundFilter m_filter = getFilterFactory().and();

    // This indicates the limits on the number of rows returned by the query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    private final List m_aliases = new ArrayList();

    DataQueryImpl(Session ssn, PersistentCollection pc) {
	this(ssn, pc.getDataSet().getQuery());
    }

    DataQueryImpl(Session ssn, Query query) {
        m_ssn = ssn;
        m_pssn = ssn.getProtoSession();
	m_original = query;
	m_query = new Query(m_original, null);
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
	} catch (UnboundParameterException e) {
	    throw new PersistenceException(e);
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
        return s_factory;
    }


    public void setOrder(String order) {
        m_query.clearOrder();
        addOrder(order);
    }

    public void addOrder(String order) {
	addOrder(order, null);
    }

    private void addOrder(String order, String def) {
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
                isAscending = parts[1].trim().startsWith("asc");
            } else {
                throw new IllegalArgumentException("bad order: " + order);
            }

	    Path p = Path.get(parts[0]);

            p = unalias(p);

	    if (def == null) {
		m_query.addOrder(p, isAscending);
	    } else {
		m_query.addOrder(p, isAscending, Path.get(def));
	    }
        }
    }

    private int m_order = 0;

    public void addOrderWithNull(String orderOne, Object orderTwo,
				 boolean isAscending) {
	Path p1, p2;

	if (orderOne == null) {
	    p1 = Path.get("__order" + m_order++);
	    m_bindings.put(p1.getPath(), null);
	} else {
	    p1 = Path.get(orderOne);
	}

	if (orderTwo instanceof String && orderTwo != null) {
	    p2 = Path.get((String) orderTwo);
	    if (!m_query.getSignature().exists(p2)) {
		p2 = Path.get("__order" + m_order++);
		m_bindings.put(p2.getPath(), orderTwo);
		if (orderOne != null) {
		    if (m_query.getSignature().getType(p1) !=
			m_pssn.getObjectType(orderTwo)) {
			throw new PersistenceException
			    ("type mismatch");
		    }
		}
	    }
	} else {
	    p2 = Path.get("__order" + m_order++);
	    m_bindings.put(p2.getPath(), orderTwo);
	}

	addOrder
            (p1.getPath() + (isAscending ? " asc" : " desc"), p2.getPath());
    }

    public void clearOrder() {
        m_query.clearOrder();
        m_order = 0;
    }

    private void setParameter(Query query, String parameterName,
			      Object value) {
        Object tobj;
	if (value == null) {
	    tobj = new java.math.BigDecimal("0");
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
	} catch (CursorException e) {
	    throw new PersistenceException(e);
	}
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
            conditions = unaliasFilter(conditions);
	    q = new Query(m_query, new PassthroughFilter(conditions));
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
	    } catch (UnboundParameterException e) {
		throw new PersistenceException(e);
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
	} catch (UnboundParameterException e) {
	    throw new PersistenceException(e);
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
	} catch (UnboundParameterException e) {
	    throw new PersistenceException(e);
	}
    }

    private final boolean propertyExists(Path path) {
        return m_query.getSignature().hasPath(path);
    }

    private String unaliasFilter(String filter) {
        SQLParser p = new SQLParser
            (new StringReader(filter),
             new SQLParser.Mapper() {
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
