package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * Element
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

abstract class Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/Element.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
