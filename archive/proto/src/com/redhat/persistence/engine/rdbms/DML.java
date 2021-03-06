package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * DML
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

abstract class DML extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/DML.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Table m_table;
    private HashMap m_bindings = new HashMap();

    public DML(Table table) {
        m_table = table;
    }

    public Table getTable() {
        return m_table;
    }

    private Path getValuePath(Column column) {
        return Path.get("__" + column.getName() + "__");
    }

    public void set(Column column, Object value) {
        Path vp = getValuePath(column);
        m_bindings.put(column, vp);
        set(vp, value, column.getType());
    }

    public Path get(Column column) {
        return (Path) m_bindings.get(column);
    }

    public Collection getColumns() {
        return m_bindings.keySet();
    }

}
