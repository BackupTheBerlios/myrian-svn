package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/02/28 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DataSet.java#6 $ by $Author: rhs $, $DateTime: 2003/02/28 17:44:25 $";

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
        return new Cursor(m_ssn, m_query);
    }

    public Cursor getCursor(Filter f) {
        if (f == null) {
            return getCursor();
        } else {
            return new Cursor(m_ssn, new Query(m_query, f));
        }
    }

    public long size() {
        return size(null);
    }

    public long size(Filter f) {
        // XXX: This is dumb, it needs to be replaced.
        Cursor c = getCursor(f);
        long result = 0;
        while (c.next()) { result++; }
        return result;
    }

}
