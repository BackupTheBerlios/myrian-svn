package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * DML
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/14 $
 **/

abstract class DML extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/DML.java#3 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

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
        set(vp, value);
    }

    public Path get(Column column) {
        return (Path) m_bindings.get(column);
    }

    public Collection getColumns() {
        return m_bindings.keySet();
    }

}
