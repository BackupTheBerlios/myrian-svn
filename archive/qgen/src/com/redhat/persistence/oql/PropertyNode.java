package com.redhat.persistence.oql;

import java.util.*;

/**
 * PropertyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

abstract class PropertyNode extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/PropertyNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Set properties = new HashSet();

    boolean update() {
        int before = properties.size();
        updateProperties();
        int size = properties.size();
        if (size < before) {
            throw new IllegalStateException("properties node shrunk");
        }
        return size > before;
    }

    abstract void updateProperties();

    public String toString() {
        return "" + properties;
    }

}
