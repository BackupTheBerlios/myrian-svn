package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #13 $ $Date: 2003/02/14 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#13 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);

    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_switch = new EventSwitch(this);

    public RDBMSEngine(Session ssn) {
        super(ssn);
    }

    void addOperation(Object obj, DML dml) {
        Object key = new CompoundKey(obj, dml.getTable());
        if (dml instanceof Delete) {
            DML prev = (DML) m_operationMap.get(key);
            if (prev != null) {
                m_operations.remove(prev);
            }
        }
        m_operationMap.put(key, dml);
        m_operations.add(dml);
    }

    void addOperation(DML dml) {
        m_operations.add(dml);
    }

    DML getOperation(Object obj, Table table) {
        Object key = new CompoundKey(obj, table);
        return (DML) m_operationMap.get(key);
    }

    void clearOperations() {
        m_operationMap.clear();
        m_operations.clear();
    }

    protected void commit() {}

    protected void rollback() {}

    public RecordSet execute(Query query) {
        if (LOG.isDebugEnabled()) {
            QGen qg = new QGen(query);
            LOG.debug(qg.generate());
        }
        return null;
    }

    public void write(Event ev) {
        if (LOG.isDebugEnabled()) {
            ev.dispatch(m_switch);
        }
    }

    public void flush() {
        for (Iterator it = m_operations.iterator(); it.hasNext(); ) {
            DML dml = (DML) it.next();
            execute(dml);
        }
        clearOperations();
    }

    private Connection getConnection() {
        throw new Error("not implemented");
    }

    private ResultSet execute(Select select) {
        SQLWriter w = new ANSIWriter();
        w.write(select);
        if (LOG.isDebugEnabled()) {
            LOG.debug(w.getSQL());
            LOG.debug(w.getBindings());
        }

        return null;
        /*
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(w.getSQL());
        w.bind(ps);
        return ps.executeQuery();*/
    }

    private int execute(DML dml) {
        SQLWriter w = new ANSIWriter();
        w.write(dml);
        if (LOG.isDebugEnabled()) {
            LOG.debug(w.getSQL());
            LOG.debug(w.getBindings());
        }

        return 0;

        /*Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(w.getSQL());
        try {
            w.bind(ps);
            return ps.executeUpdate();
        } finally {
        ps.close();
        }*/
    }

}
