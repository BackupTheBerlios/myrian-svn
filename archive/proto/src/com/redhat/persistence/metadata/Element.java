package com.redhat.persistence.metadata;

import java.util.*;

/**
 * Element
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

abstract class Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/Element.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

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
