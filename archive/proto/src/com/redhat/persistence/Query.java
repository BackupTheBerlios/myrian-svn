package com.redhat.persistence;

import com.redhat.persistence.common.*;

import java.util.*;

/**
 * Query
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class Query extends Expression {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/Query.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    private Signature m_signature;
    private Expression m_filter;
    private ArrayList m_order = new ArrayList();
    private HashSet m_ascending = new HashSet();
    private Integer m_offset = null;
    private Integer m_limit = null;
    private HashMap m_values = new HashMap();

    public Query(Signature signature, Expression filter) {
        m_signature = signature;
        m_filter = filter;
    }

    public void dispatch(Switch sw) {
        sw.onQuery(this);
    }

    private static final Expression and(Expression left, Expression right) {
	if (left == null) {
	    return right;
	}
	if (right == null) {
	    return left;
	}
	return Condition.and(left, right);
    }

    public Query(Query query, Expression filter) {
	m_signature = new Signature(query.m_signature);
	m_filter = and(query.m_filter, filter);
        m_order.addAll(query.m_order);
        m_ascending.addAll(query.m_ascending);
	m_offset = query.m_offset;
	m_limit = query.m_limit;
	m_values.putAll(query.m_values);
    }

    public Signature getSignature() {
        return m_signature;
    }

    public Expression getFilter() {
        return m_filter;
    }

    public void addOrder(Expression expr, boolean isAscending) {
        if (m_order.contains(expr)) {
            throw new IllegalArgumentException
                ("already ordered by expr: " + expr);
        }

        m_order.add(expr);
        if (isAscending) {
            m_ascending.add(expr);
        }
    }

    public Collection getOrder() {
        return m_order;
    }

    public boolean isAscending(Expression e) {
        return m_ascending.contains(e);
    }

    public void clearOrder() {
        m_order.clear();
        m_ascending.clear();
    }

    public  Integer getOffset() {
        return m_offset;
    }

    public void setOffset(Integer offset) {
        m_offset = offset;
    }

    public Integer getLimit() {
        return m_limit;
    }

    public void setLimit(Integer limit) {
        m_limit = limit;
    }

    public void set(Parameter p, Object value) {
        if (p == null) {
            throw new IllegalArgumentException("null parameter can't be set");
        }
        if (!m_signature.isParameter(p.getPath())) {
            throw new IllegalArgumentException("paramter not in signature");
        }
        m_values.put(p, value);
    }

    public Object get(Parameter p) {
        return m_values.get(p);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(m_signature + "\nfilter(" + m_filter + ")\norder(");
        for (Iterator it = m_order.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            buf.append(e);

            if (!isAscending(e)) {
                buf.append(" desc");
            }

            if (it.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append(")\nparameters");
        buf.append(m_values);
        return buf.toString();
    }

}
