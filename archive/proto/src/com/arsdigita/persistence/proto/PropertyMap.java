package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * PropertyMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

public class PropertyMap {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyMap.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 20:44:08 $";

    private ObjectType m_type;
    private HashMap m_values = new HashMap();

    public PropertyMap(ObjectType type) {
        m_type = type;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public Object get(Property prop) {
        return m_values.get(prop);
    }

    public void put(Property prop, Object obj) {
        m_values.put(prop, obj);
    }

    public Set entrySet() {
        return m_values.entrySet();
    }

}
