package com.redhat.persistence.oql;

/**
 * UnionConstraintNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class UnionConstraintNode extends ConstraintNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/UnionConstraintNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private ConstraintNode m_left;
    private ConstraintNode m_right;

    UnionConstraintNode(ConstraintNode left, ConstraintNode right) {
        m_left = left;
        m_right = right;
        add(m_left);
        add(m_right);
    }

    void updateConstraints() {
        union(m_left);
        union(m_right);
    }

}
