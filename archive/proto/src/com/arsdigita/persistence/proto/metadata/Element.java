package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * Element
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/03/05 $
 **/

abstract class Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Element.java#2 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

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
