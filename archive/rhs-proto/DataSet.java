package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/DataSet.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

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
        return m_ssn.ENGINE.execute(m_query);
    }

}
