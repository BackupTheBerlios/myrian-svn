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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.QueryType;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.metadata.Column;
import com.arsdigita.persistence.metadata.Property;

import com.arsdigita.persistence.sql.Element;
import com.arsdigita.persistence.sql.Identifier;
import com.arsdigita.persistence.sql.Clause;

import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Statement;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;

/**
 * The DataQueryImpl class encapsulates a free-form query against an object
 * model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@arsdigita.com">randyg@arsdigita.com</a>
 * @author <a href="mailto:deison@arsdigita.com">deison@arsdigita.com</a>
 * @version $Revision: #10 $ $Date: 2002/08/14 $
 */
// NOTE if we ever support anything other than forward-only,
// we'll need to shut off the auto-closing functionality
// in order to avoid re-running queries leading to different
// results and general confusion.
class DataQueryImpl extends AbstractDataOperation implements DataQuery {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataQueryImpl.java#10 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private static final Logger log =
        Logger.getLogger(DataQueryImpl.class);

    protected CompoundType m_type;
    private Operation m_op;
    private DataContainer m_data;

    private CompoundFilter m_filter;

    private String m_order = null;

    // This is the result set used by the DataQuery.  It is protected
    // so that subclasses may use it and it should only be accessed
    // by subclasses
    protected ResultSet m_rs = null;

    // this variable is used to hold the state of the data query
    // so that we know whether it should be wrapped in a view or not
    private boolean m_wrap = true;

    // flags whether the query is after the end or not
    // (if after-end, it could have been autoclosed).
    private boolean m_afterEnd = false;

    // flags if a row was fetched, e.g. query wasn't empty.
    private boolean m_notEmpty = false;

    private Throwable m_created;
    private Throwable m_lastNext = null;

    // This indicates the limits on the number of rows returned by the query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    private List m_aliases = new ArrayList();

    private static class Alias {

        private String[] m_from;
        private String[] m_to;

        public Alias(String from, String to) {
            Assert.assertNotEmpty(from, "from");
            Assert.assertNotEmpty(to, "to");

            m_from = StringUtils.split(from, '.');
            m_to = StringUtils.split(to, '.');
        }

        private static final boolean isWildcard(String[] pattern) {
            return pattern.length == 1 && pattern[0].equals("*");
        }

        public boolean isMatch(String[] path) {
            if (isWildcard(m_from)) { return true; }
            if (m_from[0].equals(path[0])) { return true; }
            return false;
        }

        public String[] unalias(String[] path) {
            String[] result;

            if (isWildcard(m_from) && isWildcard(m_to)) {
                return path;
            } else if (isWildcard(m_from) && !isWildcard(m_to)) {
                result = new String[path.length + 1];
                result[0] = m_to[0];
                System.arraycopy(path, 0, result, 1, path.length);
            } else if (!isWildcard(m_from) && isWildcard(m_to)) {
                result = new String[path.length - 1];
                System.arraycopy(path, 1, result, 0, path.length - 1);
            } else {
                result = new String[path.length];
                result[0] = m_to[0];
                System.arraycopy(path, 1, result, 1, path.length - 1);
            }

            return result;
        }

        public String toString() {
            return StringUtils.join(m_from, '.') +
                " --> " + StringUtils.join(m_to, '.');
        }

    }

    DataQueryImpl(CompoundType type, Operation op) {
        m_type = type;
        m_op = op;
        m_data = new DataContainer(m_type);
        m_created = new Throwable();
        if (type.hasOption("WRAP_QUERIES")) {
            m_wrap = type.getOption("WRAP_QUERIES").equals(Boolean.TRUE);
        }
        // this does not call "getFilterFactory()" because when this
        // is used by a DataAssociation, there is a race condition in
        // that the DataAssociationImpl needs the DataAssociationCursor
        // but the DataAssociationCursor needs a DataQuery which is
        // not created until the next line executes.  Hence, we have
        // copied the code for getFilterFactory()
        m_filter = SessionManager.getSession().getFilterFactory().and();

    }

    public CompoundType getType() {
        return m_type;
    }

    protected Operation getOperation() {
        return m_op;
    }

    public boolean hasProperty(String propertyName) {
        String[] path = StringUtils.split(propertyName, '.');
        return m_op.hasMapping(path);
    }

    protected void finalize() throws Throwable {
        if (m_rs != null) {
            StringWriter w = new StringWriter();
            PrintWriter msg = new PrintWriter(w);
            msg.println("DataQuery was not closed.");
            msg.println("DataQuery was fetched here:");
            m_created.printStackTrace(msg);
            if (m_lastNext != null) {
                msg.println("DataQuery.next() was last called here:");
                m_lastNext.printStackTrace(msg);
            }
            log.warn(w);
        }

        try {
            close();
        }
        finally {
            super.finalize();
        }
    }


    /**
     * Explicitly closes this DataQuery.
     * Query will automatically be closed when next
     * returns false, but this method should be
     * explicitly called in the case where all of the data in a query
     * is not needed (e.g. a "while (next())" loop is exited early or
     * only one value is retrieved with if (next()) {...}).
     */
    public synchronized void close() {
        if (m_rs != null) {
            try {
                // associated statement closing should be handled
                // automatically if close after use flag was set.
                m_rs.close();
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            }
            m_rs = null;
        }
        m_data.clear();
    }


    /**
     * Rewinds the data query to the beginning, i.e. it's as if next() was
     * never called.
     **/
    public void rewind() {
        close();
        m_afterEnd = false;
        m_notEmpty = false;
    }


    /**
     * Returns the data query to its initial state by rewinding it and
     * clearing any filters or ordering.
     **/
    public void reset() {
        rewind();
        clearOrder();
        clearFilter();
    }


    /**
     * Moves the cursor to the first row in the query.
     *
     * @return True if the cursor is on a valid row, false if there are no
     *         rows in the query.
     **/
    public boolean first() throws PersistenceException {
        // this will error-out due to forward-only cursor,
        // but the only correct alternative would be to close and re-open the
        // query, which may lead to the data changing and being
        // generally confusing.
        checkResultSet();
        m_data.clear();
        try {
            if (m_rs.first()) {
                populate();
                m_afterEnd = false;
                m_notEmpty = true;
                return true;
            } else {
                m_afterEnd = true;
                m_notEmpty = false;
                return false;
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }


    /**
     * Returns the value of the <i>name</i> property associated with the
     * current position in the query.
     *
     * @param propertyName The name of the property.
     *
     * @return The value of the property.
     **/
    public Object get(String propertyName) {
        if (m_afterEnd || !m_notEmpty) {
            throw new PersistenceException("Unable to retrieve value for " +
                                           propertyName + " because " +
                                           "current row is not valid");
        }

        String[] newPath = unalias(StringUtils.split(propertyName, '.'));

        return getUnalias(newPath);

    }

    protected Object getUnalias(String[] newPath) {
        Object result =
            m_data.lookupValue(newPath);

        if (result instanceof GenericDataObject) {
            result = ((GenericDataObject) result).copy();
        }

        return result;
    }

    protected Object getUnalias(String propertyName) {
        return getUnalias(StringUtils.split(propertyName, '.'));
    }


    /**
     * Returns the current position within the query. The first position is 1.
     *
     * @return The current position, 0 if there is none.
     **/
    public int getPosition() throws PersistenceException {
        if (m_afterEnd) {
            // no current position, we're past the end.
            return 0;
        } else {
            try {
                checkResultSet();
                return m_rs.getRow();
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            }
        }
    }


    /**
     * Returns true if the query has no rows.
     *
     * @return True if the query has no rows.
     **/
    public boolean isEmpty() throws PersistenceException {
        if (m_notEmpty) {
            return false;
        } else {
            try {
                checkResultSet();
                return !m_rs.isBeforeFirst() && m_rs.getRow() == 0;
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            }
        }
    }

    /**
     * Indicates whether the cursor is on the first row of the query.
     *
     * @return True if the cursor is on the first row, false otherwise.
     **/
    public boolean isBeforeFirst() throws PersistenceException {
        if (m_afterEnd) {
            return false;
        } else {
            try {
                checkResultSet();
                return m_rs.isBeforeFirst();
            } catch (java.sql.SQLException e) {
                throw PersistenceException.newInstance(e);
            }
        }
    }

    /**
     * Indicates whether the cursor is on the first row of the query.
     *
     * @return True if the cursor is on the first row, false otherwise.
     **/
    public boolean isFirst() throws PersistenceException {
        if (m_afterEnd) {
            return false;
        } else {
            try {
                checkResultSet();
                return m_rs.isFirst();
            } catch (java.sql.SQLException e) {
                throw PersistenceException.newInstance(e);
            }
        }
    }


    /**
     * Indicates whether the cursor is on the last row of the query.
     * Note: Calling the method isLast may be expensive because the
     * JDBC driver might need to fetch ahead one row in order to
     * determine whether the current row is the last row in the result
     * set.
     * <p>
     * If the query has not yet been executed, it executes the query.
     * <p>
     * This is similar to {@link com.arsdigita.db.ResultSet#isLast()}
     * <p>
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if the cursor is on the last row, false otherwise.
     **/
    public boolean isLast() throws PersistenceException {
        // no special handling for the afterEnd case because
        // a) don't want to have an annoying sometimes-it-might-throw
        // invalid-operation-exception condition
        // b) re-executing the query should result in returning false,
        // which is correct behavior even if it wasn't obtained
        // correctly.  Annoying side-effect of re-opening an auto-closed
        // query, but hopefully people won't do this much.
        try {
            checkResultSet();
            return m_rs.isLast();
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    public boolean isAfterLast() throws PersistenceException {
        return m_afterEnd;
    }


    /**
     * Moves the cursor to the last row in the query.
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if the cursor is on a valid row, false if there are no
     *         rows in the query.
     **/
    public boolean last() throws PersistenceException {
        try {
            checkResultSet();
            m_data.clear();
            if (m_rs.last()) {
                populate();
                m_afterEnd = false; // one more left
                m_notEmpty = true;
                return true;
            } else {
                m_afterEnd = true;
                m_notEmpty = false;
                return false;
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }


    /**
     * Moves to the next row in the query, returning true if more objects
     * remain, false if no rows remain.
     *
     * @return False if there are no more rows, true otherwise.
     **/
    public boolean next() throws PersistenceException {
        if (m_lastNext == null) {
            m_lastNext = new Throwable();
        } else {
            m_lastNext.fillInStackTrace();
        }

        if (m_afterEnd) {
            return false;
        } else {
            try {
                checkResultSet();
                m_data.clear();
                if (m_rs.next()) {
                    populate();
                    m_notEmpty = true;
                    if (getPosition() == m_upperBound) {
                        // if there is another row available we
                        // throw an error
                        if (m_rs.next()) {
                            close();
                            throw new PersistenceException
                                ("Query returned more than " + m_upperBound +
                                 " rows.");
                        }
                        close();
                        m_afterEnd = true;
                    }
                    return true;
                } else {
                    m_afterEnd = true;
                    if (getPosition() < m_lowerBound) {
                        close();
                        throw new PersistenceException
                            ("The query only returned " + getPosition() +
                             "rows when it should have returned at least " +
                             m_lowerBound + " rows.");
                    }
                    // we're done with this resultset.
                    close();
                    return false;
                }
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            }
        }
    }

    private void populate() {
        SessionManager.getSession().getDataStore().populate(m_data, m_op,
                                                            m_rs);
    }


    /**
     * Moves to the previous row in the query, returning true if there are any
     * rows preceeding the current row, false otherwise.
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if there are rows preceeding the current one, false
     *         otherwise.
     **/
    public boolean previous() throws PersistenceException {
        try {
            checkResultSet();
            m_data.clear();
            if (m_rs.previous()) {
                populate();
                m_afterEnd = false;
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }


    /**
     * Sets a filter for the contents of the data query.
     *
     * @param conditions The conditions for the filter.
     * @deprecated see {@link #addFilter(java.lang.String)}
     *
     * @return The filter.
     **/
    public Filter setFilter(String conditions) {
        clearFilter();
        return addFilter(conditions);
    }


    /**
     * Adds the conditions to the filter that will be used on this
     * query.  If a filter already exists, this alters the filter
     * object and returns the altered object.  If one does not
     * already exist, it creates a new filter.  When adding
     * filters, the user should not use the same parameter name
     * in multiple filters.  That is, the following will not work
     *
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority > :bound");
     * filter.set("bound", new Integer(3));
     * filter = query.addFilter("priority < :bound");
     * filter.set("bound", new Integer(8));
     * </code>
     * </pre>
     * The above actually evaluates to
     * <code>"priority < 8 and priority > 8"</code>
     * which is clearly not what the developer wants.
     * <p>
     * The following will work.
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority > :lowerBound");
     * filter.set("lowerBound", new Integer(3));
     * filter = query.addFilter("priority < :upperBound");
     * filter.set("upperBound", new Integer(8));
     * </code>
     * </pre>
     * It is actually the same as
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority > :lowerBound
     *                                  and priority < :uperBound");
     * filter.set("upperBound", new Integer(8));
     * filter.set("lowerBound", new Integer(3));
     * </code>
     * </pre>
     *
     * @param conditions The conditions for the filter.  This is a string
     *        that should be used to filter the DataQuery.  Specifically,
     *        if this is the first filter added, it appends the information
     *        on to a view-on-the-fly.  e.g.
     *        <pre><code>
     *        select * from (&lt;data query here&gt;) results
     *        where &lt;conditions here&gt;
     *        </code></pre>
     *        unless the WRAP_QUERIES option for the DataQuery is set to "false".
     *        If this is the case, the Filter is simply appended to the end of
     *        the query as follows:
     *        <pre><code>
     *        &lt;data query here&gt;)
     *        [where | or] &lt;conditions here&gt;
     *        </code></pre>
     *        It should normally take the form of
     *        <pre><code>
     *        &lt;attribute_name&gt; &lt;condition&gt; &lt;attribute bind variable&gt;
     *        </code></pre>
     *        where the "condition" is something like "=", "&lt;", "&gt;", or
     *        "!=".  The "bind variable" should be a colon followed by
     *        some attribute name that will later be set with a call to
     *        {@link com.arsdigita.persistence.Filter#set(java.lang.String,
     *               java.lang.Object)}
     *        <p>
     *        It is possible to set multiple conditions with a single
     *        addFilter statement by combining the conditions with an "and"
     *        or an "or".  Conditions may be grouped by using parentheses.
     *        Consecutive calls to addFilter append the filters using
     *        "and".
     *        <p>
     *        If there is already a filter that exists for this query
     *        then the passed in conditions are added to the current
     *        conditions with an AND like <code>(&lt;current conditions&gt;)
     *        and (&lt; passed in conditions&gt;)</code>
     *
     * @return The filter that has just been added to the query
     **/
    public Filter addFilter(String conditions) {
        if (m_rs != null) {
            throw new PersistenceException(
                                           "The filter cannot be set on an active data query. " +
                                           "Data query must be rewound.");
        }

        return m_filter.addFilter(conditions);
    }


    /**
     *  This adds the passed in filter to this query and ANDs it with
     *  an existing filters.  It returns the filter for this query.
     */
    public Filter addFilter(Filter filter) {
        if (m_rs != null) {
            throw new PersistenceException(
                                           "The filter cannot be set on an active data query. " +
                                           "Data query must be rewound.");
        }

        return m_filter.addFilter(filter);
    }

    /**
     * Removes the passed in filter from this query if it was directly
     * added to the query.  To remove a filter that was added to a
     * CompoundFilter, you must call CompoundFilter.removeFilter().
     *
     */
    public boolean removeFilter(Filter filter) {
        if (m_rs != null) {
            throw new PersistenceException(
                                           "The filter cannot be removed on an active data query. " +
                                           "Data query must be rewound.");
        }

        return m_filter.removeFilter(filter);
    }

    /**
     * <font color=red>Experimental</font>.  Highly experimental, for use
     * by permissions service only.
     */
    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return addFilter(getFilterFactory().in(propertyName, subqueryName));
    }


    /**
     * Add an 'in' subquery to a query. This version can be used with
     * subqueries which return more than 1 column as it wraps the subquery.
     * <code>subQueryProperty</code> is the column pulled out of the subquery.
     *
     * @param propertyName The column to be filtered on.
     * @param subQueryProperty The column in the subquery to be used.
     * @param queryName The fully name of a query defined in a PDL file.
     * @return The Filter object associated with this filter.
     **/
    public Filter addInSubqueryFilter( String propertyName,
                                       String subQueryProperty,
                                       String queryName ) {
        return addFilter( getFilterFactory().in( propertyName,
                                                 subQueryProperty,
                                                 queryName ) );
    }

    /**
     * <font color=red>Experimental</font>.
     */
    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        return addFilter(getFilterFactory().notIn(propertyName, subqueryName));
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
     *  <p>
     *
     *  This is simply a convenience method for
     *  <code>
     *  addFilter(getFilterFactory().equals(attribute, value));
     *  </code>
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    public Filter addEqualsFilter(String attribute, Object value) {
        return addFilter(getFilterFactory().equals(attribute, value));
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  = 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attribute !=
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attribute is not null</code>".
     *
     *  <p>
     *
     *  This is simply a convenience method for
     *  <code>
     *  addFilter(getFilterFactory().notEquals(attribute, value));
     *  </code>
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    public Filter addNotEqualsFilter(String attribute, Object value) {
        return addFilter(getFilterFactory().notEquals(attribute, value));
    }


    /**
     * Clears the current filter for the data query.
     **/
    public void clearFilter() {
        if (m_rs != null) {
            throw new PersistenceException(
                                           "Cannot clear the filter on an active data query. " +
                                           "Data query must be rewound.");
        }
        m_filter = getFilterFactory().and();
    }


    /**
     *  This retrieves the factory that is used to create the filters
     *  for this DataQuery
     */
    public FilterFactory getFilterFactory() {
        return SessionManager.getSession().getFilterFactory();
    }


    /**
     * Set the order in which the query will be returned.
     *
     * @deprecated see {@link #addOrder(java.lang.String)}
     * @param order The order.
     **/
    public void setOrder(String order) throws PersistenceException {
        clearOrder();
        addOrder(order);
    }


    /**
     * Set the order in which the result of this query will be returned. The
     * string passed is a standard SQL order by clause specified in terms of
     * the properties. For example:
     *
     * <blockquote><pre>
     * query.addOrder("creationDate desc, id");
     * </pre></blockquote>
     *
     * @param order This String parameter specifies the ordering of the
     *              output.  This should be a comma seperated list
     *              of Attribute names (not the database column names)
     *              in the order of precedence.
     *              Separating attributes by commas is the same as
     *              calling addOrder multiple times, each with the
     *              next attribute.  For instance, this
     *              <pre><code>
     *              addOrder("creationDate");
     *              addOrder("creationUser");
     *              </code></pre>
     *              is the same as
     *              <pre><code>
     *              addOrder("creationDate, creationUser");
     *              </code></pre>
     *
     *              <p>
     *              If the items should be ordered in ascending order,
     *              the attribute name should be followed by the word "asc"
     *              If the items should be ordered in descending order,
     *              the attribute should be followed by the word "desc"
     *              For instance, or order by ascending date and descending
     *              user (for users created with the same date), you would
     *              use the following:
     *              <pre><code>
     *              addOrder("creationDate asc, creationUser desc");
     *              </code></pre>
     *
     **/
    public void addOrder(String order) throws PersistenceException {
        if (m_rs != null) {
            throw new PersistenceException(
                                           "The order cannot be set on an active data query. " +
                                           "Data query must be rewound.");
        }
        if (m_order == null) {
            m_order = order;
        } else {
            m_order = m_order + ", " + order;
        }
    }


    /**
     * Clears the current order for the data query.
     **/
    public synchronized void clearOrder() {
        if (m_rs != null) {
            throw new PersistenceException(
                                           "Cannot clear the order on an active data query. " +
                                           "Data query must be rewound.");
        }
        m_order = null;
    }


    DataContainer getDataContainer() {
        return m_data;
    }


    /**
     * Returns the size of this query (i.e. the number of rows that
     * are returned). This method wraps the current sql query in a
     * <pre>select count(*) from (...)</pre>. In the future, order by
     * clauses should be removed for more efficiency.
     **/
    public long size() throws PersistenceException {
        long result;

        ResultSet rs = executeQuery(true);

        try {
            if (rs.next()) {
                result = rs.getLong(1);
            } else {
                throw new IllegalStateException(
                                                "count(*) returned no rows"
                                                );
            }
        } catch (SQLException e) {
            try {
                rs.close();
            } catch (SQLException closeException) {
                log.warn("Couldn't close result set.", closeException);
            }
            throw PersistenceException.newInstance("Error counting query", e);
        } finally {
            try {
                Statement stmt = rs.getStatement();
                if (stmt != null) { stmt.close(); }
                rs.close();
            } catch (SQLException e) {
                log.warn("Couldn't close result set.", e);
            }
        }

        return result;
    }


    /**
     *  This method allows the developer to set the range of
     *  rows desired.  Thus, the DataQuery will only return the
     *  rows between beginIndex and endIndex.  The range begins
     *  at the specified beginIndex and returns all rows after that.
     *  Thus, if a query returns 30 rows and the beginIndex is set
     *  to 6, the last 25 rows of the query will be returned.
     *
     *  @param beginIndex This is the number of the first row that
     *                    should be returned by this query.  Setting
     *                    beginIndex to 1 returns all rows.  This is
     *                    inclusive.
     */
    public void setRange(Integer beginIndex) {
        setParameter("beginFakeRowNum", beginIndex);
    }


    /**
     *  This method allows the developer to set the range of
     *  rows desired.  Thus, the DataQuery will only return the
     *  rows between beginIndex and endIndex.  The range begins
     *  at the specified beginIndex and extends to the row at index
     *  endIndex - 1. Thus the number of rows returned is
     *  endIndex-beginIndex.
     *
     *  @param beginIndex This is the number of the first row that
     *                    should be returned by this query.  Setting
     *                    beginIndex to 1 returns the rows from the
     *                    beginning.  This is inclusive.
     *  @param endIndex This is the number of the row after the last
     *                  row that should be returned.  That is, this
     *                  is exclusive (specifying beginIndex = 1 and
     *                  endIndex = 10 returns 9 rows);
     */
    public void setRange(Integer beginIndex, Integer endIndex) {
        if (endIndex.compareTo(beginIndex) <= 0) {
            throw new PersistenceException
                ("The beginIndex [" + beginIndex + "] must be strictly less " +
                 "than the endIndex [" + endIndex + "]");
        }
        setParameter("endFakeRowNum", endIndex);
        setParameter("beginFakeRowNum", beginIndex);
    }


    /**
     *  This makes sure that the member ResultSet is not null
     *  and if it is null, it retrieves a ResultSet and puts it in
     *  the meber variable.
     *
     * @return true if it had to execute the query.
     */
    private final synchronized boolean checkResultSet() {
        if (m_rs == null) {
            m_rs = executeQuery(false);
            return true;
        }
        return false;
    }


    /**
     *  This takes the member variables and executes the query, returning
     *  the resulting ResultSet
     *
     *  @return the ResultSet resulting from executing the specified query
     */
    synchronized ResultSet executeQuery(boolean count) {
        if (m_filter != null) {
            Map params = m_filter.getBindings();

            for (Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                setParameter(Filter.FILTER + me.getKey(), me.getValue());
            }
        }

        Operation operation = processOperation(count);
        ResultSet rs = SessionManager.getSession().getDataStore()
            .fireOperation(operation, m_source);
        if (rs == null) {
            throw new PersistenceException
                ("Error executing query: Result Set is Null. " +
                 Utilities.LINE_BREAK + "Query is " +
                 operation.getSQL() + Utilities.LINE_BREAK +
                 "Object is " + toString());
        } else {
            return rs;
        }
    }


    /**
     *  This returns a boolean indicating whether or not the event
     *  should be wrapped as a view before filtering.  True indicates
     *  that no view should be used.  By default, the value is
     *  false and a view is used.
     */
    public boolean isNoView() {
        return !m_wrap;
    }

    /**
     *  This sets whether or not the query should be wrapped
     *  in the standard view before filtering
     *
     *  @param isNoView This determines whether or not the
     *                  query should be wrapped in a view before
     *                  applying filters.  true means it should
     *                  NOT be wrapped
     */
    public void setNoView(boolean isNoView) {
        m_wrap = !isNoView;
    }

    private Operation processOperation(boolean count) {
        StringBuffer sql = new StringBuffer();
        StringBuffer rangeSuffix = null;

        boolean isPostgres = (com.arsdigita.db.DbHelper.getDatabase() ==
                              com.arsdigita.db.DbHelper.DB_POSTGRES);
        boolean isOracle = !isPostgres;

        // at this point, we determine what the prefix of the query
        // should be and we take care of the range, if applicable
        if (getParameter("beginFakeRowNum") != null) {
            // This means we want the sql to be something like
            //select *
            //from (select outerResults.*, rownum as fakeRownum
            //      from (" + <specified sql> + ") outerResults
            //      where rownum < :endIndex)
            // where fakeRownum >= :beginIndex"
            // --- If this is a count, we wrap the above in
            // select count(*) from (<above>)
            // down towards the end of this proc

            if (isOracle) {
                sql.append("select /*+ FIRST_ROWS */ *\n" +
                           " from (select outerResults.*, " +
                           "rownum as fakeRownum from (");
                if (m_wrap) {
                    sql.append("select * ");
                }

                rangeSuffix = new StringBuffer(") outerResults" +
                                               Utilities.LINE_BREAK);

                if (getParameter("endFakeRowNum") != null) {
                    rangeSuffix.append("where rownum < :endFakeRowNum");
                }

                rangeSuffix.append(") where fakeRownum >= :beginFakeRowNum)");
            } else {
                // it is postgres so we use "limit" and "offset"
                int begin = ((Integer)getParameter("beginFakeRowNum")).intValue();
                if (m_wrap) {
                    sql.append("select * ");
                }
                rangeSuffix = new StringBuffer(" offset " + (begin - 1));

                if (getParameter("endFakeRowNum") != null) {
                    int end = ((Integer)getParameter("endFakeRowNum")).intValue();
                    rangeSuffix.append(" limit " + (end - begin));
                }
            }
        } else {
            if (count) {
                sql.append("select count(*)" + Utilities.LINE_BREAK);
            } else if (m_wrap) {
                sql.append("select *" + Utilities.LINE_BREAK);
            }

        }

        boolean foundWhere = false;
        String filter = null;
        if (m_filter != null) {
            filter = m_filter.getSQL(this);
        }

        if (m_wrap) {
            sql.append("from (");
            sql.append(m_op.getSQL());
            sql.append(") results" + Utilities.LINE_BREAK);

            if (filter != null) {
                sql.append("where (");
                sql.append(filter);
                sql.append(")"  + Utilities.LINE_BREAK);
            }

            if (!count && m_order != null) {
                sql.append("order by " + mapSQL(m_order));
            }
        } else {
            if (count) {
                sql.append("from (");
            }

            com.arsdigita.persistence.sql.Statement stmt =
                (com.arsdigita.persistence.sql.Statement)
                Element.parse(m_op.getSQL());
            for (Iterator it = stmt.getClauses(); it.hasNext(); ) {
                Clause clause = (Clause) it.next();
                sql.append(clause.toString());
                sql.append(Utilities.LINE_BREAK);
                if (clause.isWhere()) {
                    foundWhere = true;
                } else if (clause.isFrom()) {
                    foundWhere = false;
                }
            }

            if (filter != null) {
                if (foundWhere) {
                    sql.append("and (");
                } else {
                    sql.append("where (");
                }

                sql.append(filter);
                sql.append(")" + Utilities.LINE_BREAK);
            }

            if (count) {
                sql.append(") countTable " + Utilities.LINE_BREAK);
            }

            if (!count && m_order != null) {
                sql.append("order by " + mapSQL(m_order));
            }
        }

        // This is here to add the "count" prefix around the range.
        // we cannot add this at the beginning because we actually need
        // to count the number of rows we get back after all of the
        // restrictions as opposed to before (we want to have
        // select count(*) from (<restrict query by rownum>)
        // instead of
        // select * from (<restrict query by rownum after count(*)>)
        // and we cannot just return endIndex - beginIndex because
        // we don't know if the system has endIndex rows
        if (count && getParameter("beginFakeRowNum") != null) {
            sql = new StringBuffer("select count(*) from ( " +
                                   sql.toString());
            if (rangeSuffix != null) {
                sql.append(rangeSuffix.toString());
            }
            sql.append(") countTable");
        } else {
            // this again has to deal with the "setRange" method
            if (rangeSuffix != null) {
                sql.append(rangeSuffix.toString());
            }
        }

        Operation result = new Operation(sql.toString());
        result.setFilename(m_op.getFilename() + "<filtered>");
        result.setLineInfo(m_op.getLineNumber(), m_op.getColumnNumber());
        for (Iterator it = m_op.getMappings(); it.hasNext(); ) {
            result.addMapping((Mapping) it.next());
        }
        return result;
    }

    String mapSQL(String sql) {
        return mapElement(Element.parse(sql)).toString();
    }

    Element mapElement(Element sql) {
        com.arsdigita.persistence.sql.SQL result =
            new com.arsdigita.persistence.sql.SQL();

        List leafs = sql.getLeafElements();

        for (int i = 0; i < leafs.size(); i++) {
            Element el = (Element) leafs.get(i);
            if (el.isIdentifier() &&
                !el.isBindVar() &&
                !isAllowed((Identifier) el)) {

                Element mapped = getQuery((Identifier) el);
                if (mapped == null) {
                    mapped = mapIdentifier((Identifier) el);
                }

                if (mapped == null) {
                    throw new PersistenceException
                        ("Unable to map identifier: '" + el + "'" +
                         Utilities.LINE_BREAK + "Operation: " + m_op +
                         Utilities.LINE_BREAK + m_filter +
                         Utilities.LINE_BREAK + "Order: " + m_order +
                         Utilities.LINE_BREAK + "Data: " + m_data);
                } else {
                    result.addElement(mapped);
                }
            } else {
                result.addElement(el);
            }
        }

        return result;
    }


    /**
     *  This method returns a map of all property/value pairs.  This
     *  essentially allows a single "row" of the query to be passed around.
     */
    public Map getPropertyValues() {
        Map values = new HashMap();
        Iterator iter = m_type.getProperties();
        while (iter.hasNext()) {
            String name = ((Property)iter.next()).getName();
            values.put(name, get(name));
        }
        return values;
    }


    /**
     *  This sets the upper bound on the number of rows that can be
     *  returned by this query.  If more than <code>upperBound</code>
     *  rows are returned than an error is thrown when trying
     *  to retrieve the row <code>upperBound + 1</code>
     */
    public void setReturnsUpperBound(int upperBound) {
        m_upperBound = upperBound;
    }


    /**
     *  This sets the lower bound on the number of rows that can be
     *  returned by this query.  If less than <code>lowerBound</code>
     *  rows are returned then an exception is throw after after
     *  next() is called the final time.
     */
    public void setReturnsLowerBound(int lowerBound) {
        if (lowerBound > 1 || lowerBound < 0) {
            throw new PersistenceException("The lower bound for a given query " +
                                           "must be 0 or 1 [query " +
                                           m_type.getName() + "]");
        }
        m_lowerBound = lowerBound;
    }

    /**
     *  Alias a compound property name to a different value.
     *
     *  @param fromPrefix the prefix that you're mapping from i.e.,
     *  the prefix in the PDL file.
     *  @param toPrefix the prefix that you're mapping to i.e.,
     *  the prefix that the programmer is going to use.
     */
    public void alias(String fromPrefix, String toPrefix) {
        m_aliases.add(new Alias(fromPrefix, toPrefix));
    }

    private static final Set s_allowed = new HashSet();

    static {
        s_allowed.add("asc");
        s_allowed.add("desc");
        s_allowed.add("upper");
        s_allowed.add("lower");
        s_allowed.add("nvl");
    }

    boolean isAllowed(Identifier id) {
        String[] path = id.getPath();
        return path.length == 1 && s_allowed.contains(path[0]);
    }

    Element getQuery(Identifier id) {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        QueryType type = root.getQueryType(id.toString());
        if (type != null) {
            Operation op = (Operation) type.getEvent().getOperations().next();
            return Element.parse(op.getSQL());
        } else {
            return null;
        }
    }

    Identifier mapIdentifier(Identifier id) {
        String[] newPath = unalias(id.getPath());
        for (Iterator it = m_op.getMappings(); it.hasNext(); ) {
            Mapping mapping = (Mapping) it.next();
            if (Arrays.equals(mapping.getPath(), newPath)) {
                return Identifier.getInstance(
                                              new String[] {mapping.getColumn()}
                                              );
            }
        }

        return null;
    }

    /**
     *  This prints out the SQL used by the DataQuery
     */
    public String toString() {
        StringBuffer aliases = new StringBuffer("");
        Iterator i = m_aliases.iterator();
        while ( i.hasNext() ) {
            aliases.append(i.next());
            aliases.append(Utilities.LINE_BREAK);
        }

        return "DataQuery: " + m_type + Utilities.LINE_BREAK +
            " - Order = " + m_order + Utilities.LINE_BREAK +
            " - Filter = " + m_filter + Utilities.LINE_BREAK +
            " - Operation = " + m_op + Utilities.LINE_BREAK +
            " - Data = " + m_data + Utilities.LINE_BREAK +
            " - Aliases = " + aliases.toString() + Utilities.LINE_BREAK +
            " End DataQuery";

    }

    private String[] unalias(String[] path) {
        String[] result = path;

        log.debug("External Path: " + toString(path));

        log.debug("Aliases: " + m_aliases.toString());

        for (Iterator it = m_aliases.iterator(); it.hasNext(); ) {
            Alias alias = (Alias) it.next();
            log.debug("Testing Alias: " + alias);
            if (alias.isMatch(path)) {
                log.debug("Matched");
                String[] candidate = alias.unalias(path);
                log.debug("Candidate: " + toString(candidate));
                if (propertyExists(candidate)) {
                    log.debug("Candidate exists.");
                    result = candidate;
                    break;
                } else {
                    log.debug("Candidate doesn't exist.");
                }
            } else {
                log.debug("Didn't Matched");
            }
        }

        log.debug("Internal Path: " + toString(result));

        return result;
    }

    private final boolean propertyExists(String[] path) {
        DataType type = m_data.getType();
        for (int i = 0; i < path.length; i++) {
            if (type.isCompound()) {
                Property prop = ((CompoundType) type).getProperty(path[i]);
                if (prop == null) { return false; }
                type = prop.getType();
            } else {
                return false;
            }
        }

        return true;
    }

    private static final String toString(Object[] array) {
        return Arrays.asList(array).toString();
    }

}
