package com.redhat.persistence;


/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/DataSet.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
