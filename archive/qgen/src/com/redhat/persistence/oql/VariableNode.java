package com.redhat.persistence.oql;

import java.util.*;

/**
 * VariableNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

abstract class VariableNode extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/VariableNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Map variables = new HashMap();
    Integer lower = null;
    Integer upper = null;

    boolean update() {
        int before = variables.size();
        updateVariables();
        int size = variables.size();
        if (size < before) {
            throw new IllegalStateException("set size shrunk");
        } else if (size > before) {
            lower = (Integer) Collections.min(variables.values());
            upper = (Integer) Collections.max(variables.values());
        }
        return size > before;
    }

    abstract void updateVariables();

    public String toString() {
        return "" + variables;
    }

}
