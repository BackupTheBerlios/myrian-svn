package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.Column;

import java.util.*;

/**
 * DML
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/05 $
 **/

class DML extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/DML.java#1 $ by $Author: rhs $, $DateTime: 2003/02/05 21:09:04 $";

    private Table m_table;
    private HashMap m_bindings = new HashMap();

    public DML(Table table) {
        m_table = table;
    }

    public Table getTable() {
        return m_table;
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
