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

package com.redhat.persistence;


/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/DataSet.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

    private Session m_ssn;
    private Query m_query;

    protected DataSet(Session ssn, Query query) {
        m_ssn = ssn;
        m_query = query;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Query getQuery() {
        return m_query;
    }

    public Cursor getCursor() {
        return getCursor(null);
    }

    public Cursor getCursor(Expression filter) {
	return new Cursor(m_ssn, new Query(m_query, filter));
    }

    public long size() {
        return size(null);
    }

    public long size(Expression filter) {
        m_ssn.flush();
        return m_ssn.getEngine().size(new Query(m_query, filter));
    }

    public boolean isEmpty() {
        // XXX: This could be smarter.
        return isEmpty(null);
    }

    public boolean isEmpty(Expression filter) {
        return size(filter) == 0;
    }

}
