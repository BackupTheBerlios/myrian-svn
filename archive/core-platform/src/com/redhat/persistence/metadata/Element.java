package com.redhat.persistence.metadata;

import java.util.*;

/**
 * Element
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

abstract class Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/Element.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Object m_parent;
    private Object m_key;

    Object getParent() {
        return m_parent;
    }

    void setParent(Object parent) {
        m_parent = parent;
    }

    abstract Object getElementKey();

}
