package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/11 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DataSet.java#5 $ by $Author: rhs $, $DateTime: 2003/01/11 09:31:47 $";

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
        return new Cursor(this);
    }

    public long size() {
        // XXX: This is dumb, it needs to be replaced.
        Cursor c = getCursor();
        long result = 0;
        while (c.next()) { result++; }
        return result;
    }

}
