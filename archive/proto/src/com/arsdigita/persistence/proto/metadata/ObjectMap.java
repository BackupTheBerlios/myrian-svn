package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/15 $
 **/

public class ObjectMap extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectMap.java#3 $ by $Author: rhs $, $DateTime: 2003/01/15 09:35:55 $";

    private ObjectType m_type;
    private Mist m_mappings = new Mist(this);
    private ArrayList m_key = new ArrayList();

    public ObjectMap(ObjectType type) {
        m_type = type;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public boolean hasMapping(Path p) {
        return m_mappings.containsKey(p);
    }

    public Mapping getMapping(Path p) {
        return (Mapping) m_mappings.get(p);
    }

    public void addMapping(Mapping mapping) {
        m_mappings.add(mapping);
    }

    public Collection getMappings() {
        return m_mappings;
    }

    public Collection getKeyProperties() {
        return m_key;
    }

    Object getKey() {
        return getObjectType();
    }

}
