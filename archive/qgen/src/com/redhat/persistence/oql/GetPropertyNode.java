package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * GetPropertyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class GetPropertyNode extends PropertyNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/GetPropertyNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private TypeNode m_node;
    private String m_name;

    GetPropertyNode(TypeNode node, String name) {
        m_node = node;
        m_name = name;
        add(m_node);
    }

    void updateProperties() {
        Property prop = m_node.type.getProperty(m_name);
        if (prop != null) {
            properties.add(prop);
        }
    }

}
