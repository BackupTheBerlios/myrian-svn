package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.PropertyMap;
import java.util.*;

/**
 * C
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

final class C {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/C.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    public static final com.redhat.persistence.metadata.ObjectType type(String qname) {
        return Root.getRoot().getObjectType(qname);
    }

    public static final com.redhat.persistence.metadata.ObjectType type(ObjectType type) {
        return type(type.getQualifiedName());
    }

    public static final ObjectType fromType(com.redhat.persistence.metadata.ObjectType type) {
        return MetadataRoot.getMetadataRoot().getObjectType(type.getQualifiedName());
    }

    public static final com.redhat.persistence.metadata.Property prop(Property prop) {
        return type(prop.getContainer().getQualifiedName())
            .getProperty(prop.getName());
    }

    public static final PropertyMap pmap(OID oid) {
        com.redhat.persistence.metadata.ObjectType type =
            type(oid.getObjectType());
        PropertyMap result = new PropertyMap(type);
        for (Iterator it = oid.getProperties().entrySet().iterator();
             it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            result.put(type.getProperty((String) me.getKey()), me.getValue());
        }

        return result;
    }

}
