package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * GetTypeNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class GetTypeNode extends TypeNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/GetTypeNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private TypeNode m_node;
    private String m_name;

    GetTypeNode(TypeNode node, String name) {
        m_node = node;
        m_name = name;
        add(m_node);
    }

    void updateType() {
        Property prop = m_node.type.getProperty(m_name);
        if (prop != null) { type = prop.getType(); }
    }

}
