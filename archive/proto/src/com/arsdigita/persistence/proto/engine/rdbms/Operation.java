package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/02/13 $
 **/

abstract class Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Operation.java#5 $ by $Author: rhs $, $DateTime: 2003/02/13 18:36:15 $";

    private HashMap m_values = new HashMap();

    public void set(Path parameter, Object value) {
        m_values.put(parameter, value);
    }

    public Object get(Path parameter) {
        return m_values.get(parameter);
    }

    String getParams() {
        return m_values.toString();
    }

}
