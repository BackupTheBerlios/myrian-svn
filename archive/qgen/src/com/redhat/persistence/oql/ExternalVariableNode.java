package com.redhat.persistence.oql;

import java.util.*;

/**
 * ExternalVariableNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class ExternalVariableNode extends VariableNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/ExternalVariableNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private VariableNode m_node;

    ExternalVariableNode(VariableNode node) {
        m_node = node;
        add(m_node);
    }

    void updateVariables() {
        for (Iterator it = m_node.variables.entrySet().iterator();
             it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Variable v = (Variable) me.getKey();
            Integer l = (Integer) me.getValue();
            if (l.intValue() < 1) { continue; }
            variables.put(v, new Integer(l.intValue() - 1));
        }
    }

}
