package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/17 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Operation.java#1 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

    private Table m_table;
    private OID m_oid;
    private HashMap m_bindings = new HashMap();

    protected Operation(Table table, OID oid) {
        m_table = table;
        m_oid = oid;
    }

    public Table getTable() {
        return m_table;
    }

    public OID getOID() {
        return m_oid;
    }

    public void set(Column column, Object value) {
        m_bindings.put(column, value);
    }

    public Object get(Column column) {
        return m_bindings.get(column);
    }

    public Collection getColumns() {
        return m_bindings.keySet();
    }

}
