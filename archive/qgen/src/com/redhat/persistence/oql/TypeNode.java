package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * TypeNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

abstract class TypeNode extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/TypeNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    ObjectType type;

    final boolean update() {
        if (type != null) { return false; }
        updateType();
        return type != null;
    }

    abstract void updateType();

    public String toString() {
        return "" + type;
    }

}
