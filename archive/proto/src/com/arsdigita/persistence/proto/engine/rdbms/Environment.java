package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Environment
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/26 $
 **/

class Environment {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Environment.java#1 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

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

    public String toString() {
        return m_values.toString();
    }

}
