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
 * @version $Revision: #17 $ $Date: 2003/02/19 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#17 $ by $Author: rhs $, $DateTime: 2003/02/19 22:58:51 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);

    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_switch = new EventSwitch(this);
    private ConnectionSource m_source;
    private Connection m_conn = null;

    public RDBMSEngine(ConnectionSource source) {
        m_source = source;
    }

    private void acquire() {
        if (m_conn == null) {
            m_conn = m_source.acquire();
        }
    }

    private void release() {
        if (m_conn != null) {
            m_source.release(m_conn);
            m_conn = null;
        }
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

    void addOperation(Object from, Object to, DML dml) {
        Object key =
            new CompoundKey(new CompoundKey(from, to), dml.getTable());
        m_operationMap.put(key, dml);
        m_operations.add(dml);
    }

    DML getOperation(Object obj, Table table) {
        Object key = new CompoundKey(obj, table);
        return (DML) m_operationMap.get(key);
    }

    DML getOperation(Object from, Object to, Table table) {
        Object key = new CompoundKey(new CompoundKey(from, to), table);
        return (DML) m_operationMap.get(key);
    }

    void addOperation(Object obj, StaticOperation op) {
        ArrayList ops;
        if (m_operationMap.containsKey(obj)) {
            ops = (ArrayList) m_operationMap.get(obj);
        } else {
            ops = new ArrayList();
            m_operationMap.put(obj, ops);
        }
        ops.add(op);
    }

    Collection getOperations(Object obj) {
        return (Collection) m_operationMap.get(obj);
    }

    void addOperation(StaticOperation op) {
        m_operations.add(op);
    }

    void clearOperations() {
        m_operationMap.clear();
        m_operations.clear();
    }

    protected void commit() {
        acquire();
        try {
            m_conn.commit();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        } finally {
            release();
        }
    }

    protected void rollback() {
        acquire();
        try {
            m_conn.rollback();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        } finally {
            release();
        }
    }

    public RecordSet execute(Query query) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing " + query);
        }
        QGen qg = new QGen(query);
        Select sel = qg.generate();
        return new RDBMSRecordSet(query.getSignature(), this, execute(sel),
                                  qg.getMappings(sel));
    }

    public void write(Event ev) {
        if (LOG.isDebugEnabled()) {
            ev.dispatch(m_switch);
        }
    }

    public void flush() {
        try {
            for (Iterator it = m_operations.iterator(); it.hasNext(); ) {
                DML dml = (DML) it.next();
                execute(dml);
            }
        } finally {
            clearOperations();
        }
    }

    private ResultSet execute(Select select) {
        SQLWriter w = new ANSIWriter();
        // XXX: this is a hack, for path management we need to call the
        // operation version of write.
        w.write((Operation) select);

        if (LOG.isDebugEnabled()) {
            LOG.debug(w.getSQL());
            LOG.debug(w.getBindings());
        }

        acquire();

        try {
            PreparedStatement ps = m_conn.prepareStatement(w.getSQL());
            w.bind(ps);
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    private int execute(DML dml) {
        SQLWriter w = new ANSIWriter();
        w.write(dml);

        if (LOG.isDebugEnabled()) {
            LOG.debug(w.getSQL());
            LOG.debug(w.getBindings());
        }

        acquire();

        try {
            PreparedStatement ps = m_conn.prepareStatement(w.getSQL());
            try {
                w.bind(ps);
                return ps.executeUpdate();
            } finally {
                ps.close();
            }
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    static final Object getKeyValue(Object obj) {
        if (obj == null) { return null; }
        Adapter ad = Adapter.getAdapter(obj.getClass());
        ObjectType type = ad.getObjectType(obj);
        Collection keys = type.getKeyProperties();
        if (keys.size() != 1) {
            throw new Error("not implemented");
        }
        return ad.getProperties(obj).get((Property) keys.iterator().next());
    }

}
