package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/11/27 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/DataSet.java#3 $ by $Author: rhs $, $DateTime: 2002/11/27 18:23:04 $";

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

}
