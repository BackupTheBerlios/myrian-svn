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

import com.arsdigita.persistence.metadata.CompoundType;
import java.util.Map;

/**
 * Decorate a data query so that its behavior can be changed and additional
 * methods can be added to a stock data query.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/DataQueryDecorator.java#3 $
 */
public class DataQueryDecorator implements DataQuery {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataQueryDecorator.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private DataQuery m_dq;

    /**
     * Decorate the data query <code>dq</code>.
     *
     * @param dq the data query to decorate
     */
    public DataQueryDecorator(DataQuery dq) {
        m_dq = dq;
    }

    /**
     * Retrieve the query with name <code>queryName</code> and decorate
     * it.
     *
     * @param queryName the name of the data query to decorate.
     */
    public DataQueryDecorator(String queryName) {
        this(SessionManager.getSession().retrieveQuery(queryName));
    }

    public CompoundType getType() {
        return m_dq.getType();
    }

    public boolean hasProperty(String propertyName) {
        return m_dq.hasProperty(propertyName);
    }

    // RowSequence methods
    public void rewind() {
        m_dq.rewind();
    }

    public Object get(String propertyName) {
        return m_dq.get(propertyName);
    }

    public int getPosition() {
        return m_dq.getPosition();
    }

    public boolean next() {
        return m_dq.next();
    }

    public long size() {
        return m_dq.size();
    }

    // DataQuery methods
    public void reset() {
        m_dq.reset();
    }

    public boolean first() throws PersistenceException {
        return m_dq.first();
    }

    public boolean isEmpty() throws PersistenceException {
        return m_dq.isEmpty();
    }

    public boolean isBeforeFirst() throws PersistenceException {
        return m_dq.isBeforeFirst();
    }

    public boolean isFirst() throws PersistenceException {
        return m_dq.isFirst();
    }

    public boolean isLast() throws PersistenceException {
        return m_dq.isLast();
    }

    public boolean isAfterLast() throws PersistenceException {
        return m_dq.isAfterLast();
    }

    public boolean last() throws PersistenceException {
        return m_dq.last();
    }

    public boolean previous() throws PersistenceException {
        return m_dq.previous();
    }

    public Filter setFilter(String conditions) {
        return m_dq.setFilter(conditions);
    }

    public Filter addFilter(String conditions) {
        return m_dq.addFilter(conditions);
    }

    public Filter addFilter(Filter filter) {
        return m_dq.addFilter(filter);
    }

    public boolean removeFilter(Filter filter) {
        return m_dq.removeFilter(filter);
    }

    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return m_dq.addInSubqueryFilter(propertyName, subqueryName);
    }

    public Filter addInSubqueryFilter(String property,
                                      String subQueryProperty,
                                      String subqueryName) {
        return m_dq.addInSubqueryFilter(property, subQueryProperty,
                                        subqueryName);
    }

    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        return m_dq.addNotInSubqueryFilter(propertyName, subqueryName);
    }

    public Filter addEqualsFilter(String attribute, Object value) {
        return m_dq.addEqualsFilter(attribute, value);
    }

    public Filter addNotEqualsFilter(String attribute, Object value) {
        return m_dq.addNotEqualsFilter(attribute, value);
    }

    public void clearFilter() {
        m_dq.clearFilter();
    }

    public FilterFactory getFilterFactory() {
        return m_dq.getFilterFactory();
    }

    public void setOrder(String order) throws PersistenceException {
        m_dq.setOrder(order);
    }

    public void addOrder(String order) throws PersistenceException {
        m_dq.addOrder(order);
    }

    public void clearOrder() {
        m_dq.clearOrder();
    }

    public void setParameter(String parameterName, Object value) {
        m_dq.setParameter(parameterName, value);
    }

    public Object getParameter(String parameterName) {
        return m_dq.getParameter(parameterName);
    }

    public boolean isNoView() {
        return m_dq.isNoView();
    }

    public void setNoView(boolean isNoView) {
        m_dq.setNoView(isNoView);
    }

    public void setRange(Integer beginIndex) {
        m_dq.setRange(beginIndex);
    }

    public void setRange(Integer beginIndex, Integer endIndex) {
        m_dq.setRange(beginIndex, endIndex);
    }

    public Map getPropertyValues() {
        return m_dq.getPropertyValues();
    }

    public void setReturnsUpperBound(int upperBound) {
        m_dq.setReturnsUpperBound(upperBound);
    }

    public void setReturnsLowerBound(int lowerBound) {
        m_dq.setReturnsLowerBound(lowerBound);
    }

    public void alias(String fromPrefix, String toPrefix) {
        m_dq.alias(fromPrefix, toPrefix);
    }

    public void close() {
        m_dq.close();
        // FIXME: Should we null m_dq at this point ?
    }

    public String toString() {
        return m_dq.toString();
    }

    protected DataQuery getDataQuery() {
        return m_dq;
    }
}
