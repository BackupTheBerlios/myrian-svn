/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.lang.StringBuffer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Filter is used to restrict the results of a query.  Filters can
 * be combined and manipulated to create complex queries.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/04/09 $
 */

abstract class FilterImpl implements Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/FilterImpl.java#6 $ by $Author: rhs $, $DateTime: 2003/04/09 09:48:41 $";

    private static final Logger m_log =
        Logger.getLogger(Filter.class.getName());

    private Map m_bindings = new HashMap();
    private static SQLUtilities m_util =
        SessionManager.getSQLUtilities();

    protected FilterImpl() {}


    /**
     * Returns a name that is safe to use for binding the passed in property
     * name.
     **/

    private static final String bindName(String propertyName) {
        StringBuffer result = new StringBuffer(propertyName.length());
        for (int i = 0; i < propertyName.length(); i++) {
            char c = propertyName.charAt(i);
            switch (c) {
            case '.':
                result.append('_');
                break;
            case ' ':
            case '\t':
            case '\n':
            case '\r':
            case '(':
            case ')':
                break;
            default:
                result.append(c);
                break;
            }
        }

        return result.toString();
    }


    /**
     *  Creates a new filter with the given conditions
     *
     *  @param conditions The SQL conditions that make up the heart of the
     *                    filter.  Conditions must not be null or the
     *                    empty string
     */
    public static Filter simple(String conditions) {
        if (conditions == null || conditions.equals("")) {
            throw  new PersistenceException("The filter conditions must not " +
                                            "be null or the empty string");
        }

        return new SimpleFilter(conditions);
    }


    /**
     *  This takes an attribute and returns a filter that is guranteed to
     *  return false
     */
    private static Filter filterForNullValue(String attribute,
                                             boolean trueForAllIfValueIsNull) {
        // we do not want to return null so we return something
        // that is either always true or always false
        if (trueForAllIfValueIsNull) {
            return new SimpleFilter(null);
        } else {
            // We are setting it to both null and not null because we know
            // that it is not possible to have both.
            return simple(m_util.createNullString("!=", attribute) +
                          " and " + m_util.createNullString("=", attribute));
        }
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  = 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attribute =
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attribute is null</code>".
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    protected static Filter equals(String attribute, Object value) {
        String conditions;
        if (value == null) {
            conditions = m_util.createNullString("=", attribute);
        } else {
            conditions = attribute + " = :" + bindName(attribute);
        }

        return (new SimpleFilter(conditions)).set(bindName(attribute), value);
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  != 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attribute !=
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attribute is not null</code>".
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    protected static Filter notEquals(String attribute, Object value) {
        String conditions;
        if (value == null) {
            conditions = m_util.createNullString("!=", attribute);
        } else {
            conditions = attribute + " != :" + bindName(attribute);
        }

        return (new SimpleFilter(conditions)).set(bindName(attribute), value);
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  < value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    protected static Filter lessThan(String attribute, Object value,
                                     boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, "<");
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  <= value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    protected static Filter lessThanEquals(String attribute, Object value,
                                           boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, "<=");
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  > value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of a NO-OP (true)
     *         or 1==2 (false)
     */
    protected static Filter greaterThan(String attribute, Object value,
                                        boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, ">");
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  >= value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of a NO-OP (true)
     *         or 1==2 (false)
     */
    protected static Filter greaterThanEquals(String attribute, Object value,
                                              boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, ">=");
    }


    /**
     *  This actually creates the filter for lessThan, lessThanEquals,
     *  greaterThan, and greaterThanEquals
     */
    private static Filter createComparisonFilter
        (String attribute, Object value, boolean trueForAllIfValueIsNull,
         String comparator) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            Filter filter = simple(attribute + " " + comparator + " :" +
                                   bindName(attribute));
            filter.set(bindName(attribute), value);
            return filter;
        }

    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  like 'value%'</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    protected static Filter startsWith(String attribute, String value,
                                       boolean trueForAllIfValueIsNull) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            Filter filter = simple(attribute + " like :" +
                                   bindName(attribute) + " || '%'");
            filter.set(bindName(attribute), value);
            return filter;
        }
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  like '%value'</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    protected static Filter endsWith(String attribute, String value,
                                     boolean trueForAllIfValueIsNull) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            Filter filter = simple(attribute + " like '%' || :" +
                                   bindName(attribute));
            filter.set(bindName(attribute), value);
            return filter;
        }
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  like '%value%'</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    protected static Filter contains(String attribute, String value,
                                     boolean trueForAllIfValueIsNull) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            Filter filter = simple(attribute + " like '%' || :" +
                                   bindName(attribute) + " || '%'");
            filter.set(bindName(attribute), value);
            return filter;
        }
    }


    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/

    protected static Filter in(String propertyName, String queryName) {
        if (propertyName == null || propertyName.equals("") ||
            queryName == null || queryName.equals("")) {
            throw new IllegalArgumentException
		("The propertyName and queryName must be non empty.");
        }

        return new InFilter(propertyName, null, queryName);
    }

    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property to be filtered on and subquery. subQueryProperty is the
     * property in the subquery which relates to the property being filtered
     * on. The subquery must be a fully qualified query name of a query defined
     * in a PDL file somewhere.
     **/

    protected static Filter in(String property, String subQueryProperty,
			       String queryName) {
        if (property == null || property.equals("") ||
            subQueryProperty == null || subQueryProperty.equals("") ||
            queryName == null || queryName.equals("") ) {
            throw new IllegalArgumentException
                ("The property, subQueryProperty and queryName must be " +
                 "non empty.");
        }

        return new InFilter(property, subQueryProperty, queryName);
    }

    /**
     * This creates a filter that constructs a "not in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/

    protected static Filter notIn(String propertyName, String queryName) {
        if (propertyName == null || propertyName.equals("") ||
            queryName == null || queryName.equals("")) {
            throw new IllegalArgumentException
		("The propertyName and queryName must be non empty.");
        }

	final InFilter in = new InFilter(propertyName, null, queryName);

        return new FilterImpl() {
		public String getConditions() {
		    return "not " + in.getConditions();
		}
	    };
    }


    /**
     *  Sets the values of the bind variables in the Filter.
     *
     *  @param parameterName The name of the bind variable
     *  @param value The value to substitute in for the bind variable.
     *
     *  @pre value == null || value instanceof Number || value instanceof String
     *  @return returns <code>this</code>
     *
     *  @throws PersistenceException if value type unsupported.
     */
    public Filter set(String parameterName, Object value) {
        if (m_bindings.containsKey(parameterName)) {
	    if (m_log.isEnabledFor(Level.WARN)) {
		m_log.warn("The existing filter already contains a parameter " +
			   "named \"" + parameterName + "\".  Overwriting the" +
			   " the old value " + m_bindings.get(parameterName) +
			   " with " + value);
	    }
        } 
        m_bindings.put(parameterName, value);
        return this;
    }


    /**
     *  This returns the bindings for this Filter.  That is, it returns
     *  a map of key (variable name) - value (variable value) pairs
     *
     *  @return a map of key (variable name) - value (variable value) pairs
     */
    public Map getBindings() {
        return m_bindings;
    }


    /**
     *  This adds an item to the bindings.  <b>This should ONLY be used
     *  by classes extending FilterImpl</b>
     *
     *  @param key The key (attribute name) for the new binding
     *  @param value the value for the new binding
     */
    protected void addBinding(String key, Object value) {
        m_bindings.put(key, value);
    }


    /**
     *  This a Map to the bindings.  <b>This should ONLY be used
     *  by classes extending FilterImpl</b>
     *  @param bindings A map of attribute/value pairs to add to the
     *  bindings of the filter
     */
    protected void addBindings(Map bindings) {
        m_bindings.putAll(bindings);
    }

}
