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

/**
 * Filter is used to restrict the results of a query.  Filters can
 * be combined and manipulated to create complex queries.
 *
 *    <p>
 *    It is important to realize that Filters are just that; they
 *    filter the resulting data from the query.  For instance, if you
 *    have:
 *    </p>
 *    <pre><code>
 *
 *query myDataQuery {
 *  do {
 *     select max(article_id) from articles
 *  } map {
 *     articleID = articles.article_id;
 *  }
 *}</code></pre>
 *
 *      <p>
 *      and then add a the filter "lower(title) like 'b%'"
 *      the new query will be
 *      </p>
 *      <pre><code>
 * select *
 * from (select max(article_id) from articles) results
 * where lower(title) like 'b%'
 * </code></pre>
 *        <p>and not</p>
 *        <pre><code>
 * select max(article_id) from articles where lower(title) like 'b%'
 * </code></pre>
 *
 *        <p>This can clearly lead to different results.</p>
 *  <p>However, it is possible to get the query you want by setting
 *  the WRAP_QUERIES option to false.  You can declare this value within
 *  the "options" block of the data query.  For example,</p>
 *    <pre><code>
 *
 *query myDataQuery {
 *  options {
 *           WRAP_QUERIES = false;
 *  }
 *  do {
 *     select max(article_id), title from articles
 *  } map {
 *     articleID = articles.article_id;
 *  }
 *}</code></pre>
 *
 * <p>
 * It is also important to note that any attribute used within a filter
 * MUST appear within the "map" section of the query definition.  This
 * is because the filter must be able to map the attribute name to the
 * correct column and assuming that an attribute name is the same as
 * the column name is not sufficient.  So, filtering the above query
 * by the "title" column will error.
 *
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 */

public interface Filter {

    String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/Filter.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    // this string is used as the namespace for the bind variables
    String FILTER = "__FILTERPARAMS__";


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
    Filter set(String parameterName, Object value);


    /**
     *  This returns the bindings for this Filter.  That is, it returns
     *  a map of key (variable name) - value (variable value) pairs
     *
     *  @return a map of key (variable name) - value (variable value) pairs
     */
    Map getBindings();


    /**
     *  This returns the string representation of this Filter before
     *  any bindings are applied
     */
    String getConditions();

}
