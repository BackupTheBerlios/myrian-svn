package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.*;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

class RDBMSRecordSet extends RecordSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSRecordSet.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private static final Logger LOG = Logger.getLogger(RecordSet.class);

    final private RDBMSEngine m_engine;
    private ResultSet m_rs;
    final private Map m_mappings;

    RDBMSRecordSet(Signature sig, RDBMSEngine engine, ResultSet rs,
                   Map mappings) {
        super(sig);
	if (rs == null) {
	    throw new IllegalArgumentException("null result set");
	}
        m_engine = engine;
        m_rs = rs;
        m_mappings = mappings;
    }

    ResultSet getResultSet() {
        return m_rs;
    }

    String getColumn(Path p) {
        return (String) m_mappings.get(p);
    }

    public boolean next() {
	if (m_rs == null) {
	    throw new IllegalStateException("result set closed");
	}
        try {
            return m_rs.next();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    public Object get(Path p) {
        try {
            Adapter ad = Adapter.getAdapter
                (getSignature().getProperty(p).getType());
            return ad.fetch(m_rs, getColumn(p));
        } catch (SQLException e) {
            throw new Error
                ("error fetching path (" + p + "): " + e.getMessage());
        }
    }

    public void close() {
        if (m_rs == null) { return; }
        try {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("Closing Statement because resultset was closed.");
	    }
	    m_rs.getStatement().close();
            m_rs.close();
            m_rs = null;
	    m_engine.release();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

}
