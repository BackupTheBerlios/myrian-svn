/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public interface Filter {

    String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/Filter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
