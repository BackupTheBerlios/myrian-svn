package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * ObjectMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ObjectMap {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectMap.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private Root m_root;
    private ObjectType m_type;
    private ArrayList m_mappings = new ArrayList();
    private HashMap m_mappingsMap = new HashMap();

    public ObjectMap(ObjectType type) {
        m_type = type;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    void setRoot(Root root) {
        m_root = root;
    }

    public Root getRoot() {
        return m_root;
    }

    public boolean hasMapping(Path p) {
        return m_mappingsMap.containsKey(p);
    }

    public Mapping getMapping(Path p) {
        if (p == null) {
            throw new IllegalArgumentException
                ("Cannot get a mapping for a null path");
        }
        return (Mapping) m_mappingsMap.get(p);
    }

    public void addMapping(Mapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException
                ("Cannot add a null mapping");
        }
        if (hasMapping(mapping.getPath())) {
            throw new IllegalArgumentException
                ("Already have a mapping for path: " + mapping.getPath());
        }
        if (mapping.getObjectMap() != null) {
            throw new IllegalArgumentException
                ("Mapping already belongs to an object map: " + mapping);
        }

        m_mappings.add(mapping);
        m_mappingsMap.put(mapping.getPath(), mapping);
        mapping.setObjectMap(this);
    }

    public Collection getMappings() {
        return m_mappings;
    }

}
