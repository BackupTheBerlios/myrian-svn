package com.redhat.persistence.oql;

/**
 * ExternalConstraintNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class ExternalConstraintNode extends ConstraintNode {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/ExternalConstraintNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private ConstraintNode m_node;

    ExternalConstraintNode(ConstraintNode node) {
        m_node = node;
    }

    void updateConstraints() {
        
    }

}
