package com.redhat.persistence.oql;

import java.util.*;

/**
 * Node
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/20 $
 **/

abstract class Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Node.java#2 $ by $Author: rhs $, $DateTime: 2004/01/20 12:41:29 $";

    private List m_outputs = new ArrayList();

    Node() {}

    void add(Node input) {
        if (!input.m_outputs.contains(this)) {
            input.m_outputs.add(this);
        }
    }

    Collection getOutputs() {
        return m_outputs;
    }

    abstract boolean update();

}
