/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.redhat.persistence.oql.And;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Or;
import com.redhat.persistence.oql.Static;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * CompoundFilters are used to AND or OR multiple filters together.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/03/22 $
 */

class CompoundFilterImpl extends FilterImpl implements CompoundFilter {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/CompoundFilterImpl.java#2 $ by $Author: ashah $, $DateTime: 2004/03/22 16:48:51 $";

    private static final Logger m_log =
        Logger.getLogger(CompoundFilterImpl.class);

    private boolean m_isAnd;
    private ArrayList m_filters = new ArrayList();

    /**
     *  This creates a new compound filter with the specified join type.
     */
    private CompoundFilterImpl(boolean isAnd) {
        m_isAnd = isAnd;
    }


    /**
     *  Creates a filter that will AND together all filters passed in to it
     *  For instance, if developers want to combine two filters in to one,
     *  they can write
     *  <pre><code>
     *  CompoundFilter.and().addFilter(Filter.string("lastName", lname))
     .addFilter(Filter.string("firstName", fname))
     *  </code></pre>
     *
     */
    public static CompoundFilter and() {
        return new CompoundFilterImpl(true);
    };


    /**
     *  Creates a filter that will AND together all filters passed in to it
     *  For instance, if developers want to combine two filters in to one,
     *  they can write
     *  <pre><code>
     *  CompountFilter.or().addFilter(Filter.string("lastName", keyword))
     .addFilter(Filter.string("firstName", keyword))
     *  </code></pre>
     *
     */
    public static CompoundFilter or() {
        return new CompoundFilterImpl(false);
    };

    /**
     *  This provides a mechanism for adding conditions to the existing
     *  filter.  This appends the passed in conditions to the existing
     *  conditions with an "and" statement.
     *  There is no way to remove conditions.
     *
     *  @param conditions The conditions to add to this filter
     */
    public CompoundFilter addFilter(String conditions) {
        addFilter(FilterImpl.simple(conditions));
        return this;
    }


    /**
     *  This adds the passed in filter to this query and adds it
     *  according to the type of filter this is (if it was created
     *  using Filter.or() then it ORs this filter with the existing
     *  ones; otherwise it ANDs it);
     *
     *  @return this
     */
    public CompoundFilter addFilter(Filter filter) {

        if (m_filters.contains(filter)) {
            // the filter was already added, so do nothing.
            return this;
        }

        Map bindings = filter.getBindings();
        if (bindings != null) {
            addBindings(bindings);
        }

        m_filters.add(filter);

        return this;
    }

    public boolean removeFilter(Filter filter) {
        return m_filters.remove(filter);
    }

    protected Expression makeExpression(DataQueryImpl query, Map bindings) {
        if (m_filters.size() == 0) {
            return null;
        }

        Expression expr = null;

        for (Iterator it = m_filters.iterator(); it.hasNext(); ) {
            Filter filter = (Filter) it.next();
            Expression fExpr = null;
            if (filter instanceof FilterImpl) {
                Map map;
                if (bindings.size() > 0) {
                    map = new HashMap();
                    map.putAll(bindings);
                    map.putAll(getBindings());
                } else {
                    map = getBindings();
                }
                fExpr = ((FilterImpl) filter).makeExpression(query, map);
            } else {
                String conditions = filter.getConditions();
                if (conditions != null) {
                    fExpr = new Static(conditions, filter.getBindings());
                }
            }

            if (expr == null) {
                expr = fExpr;
            } else if (fExpr != null) {
                if (m_isAnd) {
                    expr = new And(expr, fExpr);
                } else {
                    expr = new Or(expr, fExpr);
                }
            }
        }

        return expr;
    }

    private final String combineWith() {
        return m_isAnd ? "and" : "or";
    }

    public String getConditions() {
	if (m_filters.size() == 0) {
	    return null;
	}

	StringBuffer result = new StringBuffer();

	boolean first = true;

	for (Iterator it = m_filters.iterator(); it.hasNext(); ) {
	    Filter f = (Filter) it.next();
	    String sql = f.getConditions();
	    if (sql == null || sql.equals("")) {
		continue;
	    }

	    if (first) {
		first = false;
	    } else {
		result.append(" " + combineWith() + " ");
	    }

            result.append("(");
	    result.append(sql);
            result.append(")");
	}

	if (first) {
	    return null;
	}

	return result.toString();
    }

    /**
     *  This outputs a string representation of the CompoundFilter
     */
    public String toString() {
	StringBuffer result = new StringBuffer();

	result.append("(");

	boolean first = true;

	for (Iterator it = m_filters.iterator(); it.hasNext(); ) {
	    Filter f = (Filter) it.next();
	    if (first) {
		first = false;
	    } else {
		result.append(" " + combineWith() + " ");
	    }

	    result.append(f.toString());
	}

	result.append(")");

        return result.toString();
    }

}
