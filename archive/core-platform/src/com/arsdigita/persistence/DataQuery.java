/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.infrastructure.RowSequence;
import java.util.Map;

/**
 * An instance of the DataQuery class may be used to access the results of a
 * named query. It is typically used in the following manner:
 *
 * <blockquote><pre>
 * DataQuery query = session.retrieveQuery("MyQuery");
 *
 * Filter f = query.addEqualsFilter("myProperty", value);
 *
 * query.addOrder("creationDate desc");
 *
 * int numLines = query.size();
 * System.out.println("Lines: " + numLines);
 *
 * while (query.next()) {
 *   Object prop = query.get("myProperty");
 *   System.out.println("MyProperty: " + prop);
 * }
 * </pre></blockquote>
 *
 * Named queries are defined in a PDL file using the following syntax:
 * <pre><blockquote>
 * query MyQuery {
 *     do {
 *         select *
 *         from my_table;
 *     } map {
 *         myProperty = my_table.my_column;
 *         creationDate = my_table.creation_date;
 *     }
 * }
 * </blockquote>
 * </pre>
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public interface DataQuery extends RowSequence {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataQuery.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     * Returns the type of this data query.
     **/
    CompoundType getType();


    /**
     * Returns true if this query fetches the given property.
     *
     * @param propertyName A property name.
     * @return True if this query fetches the given property.
     **/
    boolean hasProperty(String propertyName);


    /**
     * Returns the data query to its initial state by rewinding it and
     * clearing any filters or ordering.
     **/
    void reset();


    /**
     * Moves the cursor to the first row in the query. 
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the cursor is on a valid row; false if there are no
     *         rows in the query.
     *
     * @exception PersistenceException Always thrown!
     **/
    boolean first() throws PersistenceException;


    /**
     * Returns true if the query has no rows.
     *
     * @return true if the query has no rows; false otherwise
     **/
    boolean isEmpty() throws PersistenceException;


    /**
     * Indicates whether the cursor is before the first row of the query.
     *
     * @return true if the cursor is before the first row; false if
     *  the cursor is at any other position or the result set contains
     *  rows.
     **/
    boolean isBeforeFirst() throws PersistenceException;

    /**
     * Indicates whether the cursor is on the first row of the query.
     *
     * @return true if the cursor is on the first row; false otherwise
     **/
    boolean isFirst() throws PersistenceException;


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
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if the cursor is on the last row, false otherwise.
     **/
    boolean isLast() throws PersistenceException;


    /**
     * Indicates whether the cursor is after the last row of the query.
     *
     * @return True if the cursor is after the last row, false if the
     *         cursor is at any other position or the result set contains
     *         no rows.
     **/
    boolean isAfterLast() throws PersistenceException;


    /**
     * Moves the cursor to the last row in the query.
     * <font color=red>Not implemented yet.</font>
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the new current row is valid; false if there are no
     *         rows in the query
     * @exception PersistenceException Always thrown!
     **/
    boolean last() throws PersistenceException;


    /**
     * Moves to the previous row in the query.
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the new current row is valid; false otherwise
     * @exception PersistenceException Always thrown!
     **/
    boolean previous() throws PersistenceException;


    /**
     * Sets a filter for this query. The filter consists of a set of SQL
     * condition specified in terms of the properties of this query. The
     * conditions may be combined with "and" and "or". Bind variables may be
     * used in the body of the filter. The values are set by using the set
     * method on the Filter object that is returned.
     *
     * <blockquote><pre>
     * Filter f = query.setFilter("id < :maxId and id > :minId");
     * f.set("maxId", 10);
     * f.set("minId", 1);
     * </pre></blockquote>
     *
     * @param conditions the conditions for the filter
     * @deprecated see {@link #addFilter(java.lang.String)}
     *
     * @return the newly created filter for this query
     **/
    Filter setFilter(String conditions);


    /**
     * Adds the conditions to the filter that will be used on this
     * query.  If a filter already exists, this alters the filter
     * object and returns the altered object.  If one does not
     * already exist, it creates a new filter.  When adding filters
     * the user should be aware that their query is wrapped and the
     * filter is appended to the wrapped query.  That is, your query
     * will look like the following:
     *        <pre><code>
     *        select * from (&lt;data query here&gt;) results
     *        where &lt;conditions here&gt;
     *        </code></pre>
     *<p>
     *
     * When adding
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

    Filter addFilter(String conditions);


    /**
     *  This adds the passed in filter to this query and ANDs it with
     *  an existing filters.  It returns the filter for this query.
     */
    Filter addFilter(Filter filter);

    /**
     * Removes the passed in filter from this query if it was directly
     * added to the query.  To remove a filter that was added to a
     * CompoundFilter, you must call CompoundFilter.removeFilter().
     * 
     */
    boolean removeFilter(Filter filter);

    /**
     * <font color=red>Experimental</font>.  Highly experimental, for use
     * by permissions service only.
     */
    Filter addInSubqueryFilter(String propertyName, String subqueryName);


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
    Filter addInSubqueryFilter( String propertyName,
				String subQueryProperty,
				String queryName );

    /**
     * <font color=red>Experimental</font>.
     */
    Filter addNotInSubqueryFilter(String propertyName, String subqueryName);

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
    Filter addEqualsFilter(String attribute, Object value);
     

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
    Filter addNotEqualsFilter(String attribute, Object value);


    /**
     * Clears the current filter for the data query.
     **/
    void clearFilter();


    /**
     *  This retrieves the factory that is used to create the filters
     *  for this DataQuery
     */
    FilterFactory getFilterFactory();


    /**
     * Set the order in which the result of this query will be returned. The
     * string passed is a standard SQL order by clause specified in terms of
     * the properties. For example:
     *
     * <blockquote><pre>
     * query.setOrder("creationDate desc, id");
     * </pre></blockquote>
     * @deprecated see {@link #addOrder(java.lang.String)}
     **/
    void setOrder(String order) throws PersistenceException;


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
    void addOrder(String order) throws PersistenceException;


    /**
     * Clears the current order clause for the data query.
     **/
    void clearOrder();


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    void setParameter(String parameterName, Object value);


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName);


    /**
     *  This returns a boolean indicating whether or not the event
     *  should be wrapped as a view before filtering.  True indicates
     *  that no view should be used.  By default, the value is
     *  false and a view is used.
     */
    boolean isNoView();

    /**
     *  This sets whether or not the query should be wrapped
     *  in the standard view before filtering
     *  
     *  @param isNoView This determines whether or not the
     *                  query should be wrapped in a view before
     *                  applying filters.  true means it should
     *                  NOT be wrapped
     */
    void setNoView(boolean isNoView);


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
    void setRange(Integer beginIndex);

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
     *  @exception A PersistenceException is thrown if 
     *             endIndex <= beginIndex
     */
    void setRange(Integer beginIndex, Integer endIndex);


    /**
     *  This method returns a map of all property/value pairs.  This
     *  essentially allows a single "row" of the query to be passed around.
     */
    public Map getPropertyValues();


    /**
     *  This sets the upper bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsUpperBound(int upperBound);

    
    /**
     *  This sets the lower bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsLowerBound(int lowerBound);

    /**
     *  Alias a compound property name to a different value. Use the
     *  empty string ("") to add a prefix to all compound property
     *  names that don't match any other aliases.
     *
     *  @param fromPrefix the prefix that you're mapping from i.e.,
     *  the prefix in the PDL file.
     *  @param toPrefix the prefix that you're mapping to i.e.,
     *  the prefix that the programmer is going to use.  */
    public void alias(String fromPrefix, String toPrefix);

    /**
     * Explicitly closes this DataQuery.  
     * Query should automatically be closed when next
     * returns false, but this method should be 
     * explicitly called in the case where all of the data in a query 
     * is not needed (e.g. a "while (next())" loop is exited early or
     * only one value is retrieved with if (next()) {...}).
     */    
    void close();
}
