package com.arsdigita.persistence.proto.metadata;

import java.util.*;


/**
 * Root
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/02 $
 **/

public class Root {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Root.java#2 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    private static final Root ROOT = new Root();

    public static final Root getRoot() {
        return ROOT;
    }

    private ArrayList m_types = new ArrayList();
    private HashMap m_typeMap = new HashMap();
    private HashMap m_objectMaps = new HashMap();

    private Root() {}

    public boolean hasObjectType(String qualifiedName) {
        return m_typeMap.containsKey(qualifiedName);
    }

    public void addObjectType(ObjectType type) {
        if (type == null) {
            throw new IllegalArgumentException
                ("Cannot add a null type to the Root");
        }
        if (hasObjectType(type.getQualifiedName())) {
            throw new IllegalArgumentException
                ("Root already contains a type named: " +
                 type.getQualifiedName());
        }
        if (type.getRoot() != null) {
            throw new IllegalArgumentException
                ("Type belongs to another Root: " + type);
        }

        m_types.add(type);
        m_typeMap.put(type.getQualifiedName(), type);
        type.setRoot(this);
    }

    public ObjectType getObjectType(String qualifiedName) {
        return (ObjectType) m_typeMap.get(qualifiedName);
    }

    public Collection getObjectTypes() {
        return m_types;
    }

    public ObjectMap getObjectMap(ObjectType type) {
        return (ObjectMap) m_objectMaps.get(type);
    }

    public void addObjectMap(ObjectMap map) {
        if (map == null) {
            throw new IllegalArgumentException
                ("Cannot add null object map.");
        }
        if (m_objectMaps.containsKey(map.getObjectType())) {
            throw new IllegalArgumentException
                ("Root already contains object map for type: " +
                 map.getObjectType().getQualifiedName());
        }
        if (map.getRoot() != null) {
            throw new IllegalArgumentException
                ("Map belongs to another Root: " + map);
        }

        m_objectMaps.put(map.getObjectType(), map);
        map.setRoot(this);
    }

}
