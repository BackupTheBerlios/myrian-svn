package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;
import java.sql.*;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/14 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Operation.java#6 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    private HashMap m_values = new HashMap();

    public boolean isParameter(Path path) {
        return m_values.containsKey(path);
    }

    public void set(Path parameter, Object value) {
        m_values.put(parameter, value);
    }

    public Object get(Path parameter) {
        return m_values.get(parameter);
    }

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new ANSIWriter();
        w.write(this);
        return w.getSQL() + "\n" + w.getBindings();
    }

}
