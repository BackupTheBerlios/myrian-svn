package com.redhat.persistence.engine.rdbms;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.SQLExceptionHandler;
import com.redhat.persistence.common.*;
import com.redhat.persistence.*;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/08 $
 **/

class RDBMSRecordSet extends RecordSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSRecordSet.java#3 $ by $Author: bche $, $DateTime: 2003/08/08 11:00:21 $";

    final private RDBMSEngine m_engine;
    final private ResultCycle m_rc;
    final private Map m_mappings;

    RDBMSRecordSet(Signature sig, RDBMSEngine engine, ResultCycle rc,
                   Map mappings) {
        super(sig);
	if (rc == null) {
	    throw new IllegalArgumentException("null result set");
	}
        m_engine = engine;
        m_rc = rc;
        m_mappings = mappings;
    }

    ResultSet getResultSet() {
        return m_rc.getResultSet();
    }

    String getColumn(Path p) {
        return (String) m_mappings.get(p);
    }

    public boolean next() {
        return m_rc.next();
    }

    public Object get(Path p) {
        try {
            Adapter ad = Adapter.getAdapter
                (getSignature().getProperty(p).getType());

            StatementLifecycle cycle = m_rc.getLifecycle();
            String column = getColumn(p);
            if (cycle != null) { cycle.beginGet(column); }
            Object result = ad.fetch(m_rc.getResultSet(), column);
            if (cycle != null) { cycle.endGet(result); }
            return result;
        } catch (SQLException e) {
            //robust connection pooling
            m_engine.checkBadConnection(e);
            throw new Error
                ("error fetching path (" + p + "): " + e.getMessage());
        }
    }

    public void close() {
        m_rc.close();
    }

}
