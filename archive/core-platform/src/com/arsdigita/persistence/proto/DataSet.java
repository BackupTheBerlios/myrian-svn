package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/DataSet.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
        // XXX: This is dumb, it needs to be replaced.
        Cursor c = getCursor(filter);
        long result = 0;
        while (c.next()) { result++; }
        return result;
    }

    public boolean isEmpty() {
        // XXX: This could be smarter.
        return isEmpty(null);
    }

    public boolean isEmpty(Expression filter) {
        return size(filter) == 0;
    }

}
