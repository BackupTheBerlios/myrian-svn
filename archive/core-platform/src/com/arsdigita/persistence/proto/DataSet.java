package com.arsdigita.persistence.proto;


/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/07/07 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/DataSet.java#3 $ by $Author: vadim $, $DateTime: 2003/07/07 12:16:50 $";

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
