package com.arsdigita.persistence.proto.metadata;

import java.util.*;


/**
 * Root
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Root {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Root.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private static final Root ROOT = new Root();

    public static final Root getRoot() {
        return ROOT;
    }

    private ArrayList m_types = new ArrayList();
    private HashMap m_typeMap = new HashMap();

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
    }

    public Collection getObjectTypes() {
        return m_types;
    }

}
