package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * PropertyMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/13 $
 **/

public class PropertyMap {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyMap.java#1 $ by $Author: rhs $, $DateTime: 2003/02/13 13:54:43 $";

    public static final PropertyMap EMPTY = new PropertyMap() {
            public void set(PropertyMap prop, Object obj) {
                throw new IllegalStateException("don't mutate me");
            }
        };

    private HashMap m_values = new HashMap();

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
