package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2003/02/07 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#11 $ by $Author: rhs $, $DateTime: 2003/02/07 16:00:49 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);

    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();
    private EventSwitch m_switch = new EventSwitch(this);

    public RDBMSEngine(Session ssn) {
        super(ssn);
    }

    void addOperation(OID oid, DML dml) {
        String key = oid + ":" + dml.getTable().getName();
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

    DML getOperation(OID oid, Table table) {
        return (DML) m_operationMap.get(oid + ":" + table.getName());
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
        ev.dispatch(m_switch);
    }

    public void flush() {
        if (LOG.isDebugEnabled()) {
            LOG.debug(m_operations);
        }
        clearOperations();
    }

}
