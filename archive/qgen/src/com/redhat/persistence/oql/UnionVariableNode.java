package com.redhat.persistence.oql;

/**
 * UnionVariableNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class UnionVariableNode extends VariableNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/UnionVariableNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private VariableNode m_left;
    private VariableNode m_right;

    UnionVariableNode(VariableNode left, VariableNode right) {
        m_left = left;
        m_right = right;
        add(m_left);
        add(m_right);
    }

    void updateVariables() {
        variables.putAll(m_left.variables);
        variables.putAll(m_right.variables);
    }

}
