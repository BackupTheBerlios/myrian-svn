package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * GetKeyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class GetKeyNode extends KeyNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/GetKeyNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private KeyNode m_keys;
    private TypeNode m_type;
    private String m_name;

    GetKeyNode(KeyNode keys, TypeNode type, String name) {
        m_keys = keys;
        m_type = type;
        m_name = name;
        add(m_keys);
        add(m_type);
    }

    void updateKeys() {
        Property prop = m_type.type.getProperty(m_name);
        if (prop == null) { return; }
        if (m_keys.contains(Collections.singleton(prop))
            || m_keys.contains(Collections.EMPTY_LIST)) {
            addAll(Expression.getKeys(prop.getType()));
        }
    }

}
