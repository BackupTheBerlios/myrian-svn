package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.*;

import java.util.*;
import java.sql.*;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/17 $
 **/

class RDBMSRecordSet extends RecordSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSRecordSet.java#2 $ by $Author: rhs $, $DateTime: 2003/02/17 20:13:29 $";

    final private RDBMSEngine m_engine;
    final private ResultSet m_rs;
    final private Map m_mappings;

    RDBMSRecordSet(Signature sig, RDBMSEngine engine, ResultSet rs,
                   Map mappings) {
        super(sig);
        m_engine = engine;
        m_rs = rs;
        m_mappings = mappings;
    }

    public boolean next() {
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
            return ad.fetch(m_rs, (String) m_mappings.get(p));
        } catch (SQLException e) {
            throw new Error
                ("error fetching path (" + p + "): " + e.getMessage());
        }
    }

}
