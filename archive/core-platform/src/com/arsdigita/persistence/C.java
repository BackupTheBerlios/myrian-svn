package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.metadata.Root;
import com.arsdigita.persistence.proto.PropertyMap;
import java.util.*;

/**
 * C
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

final class C {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/C.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final com.arsdigita.persistence.proto.metadata.ObjectType type(String qname) {
        return Root.getRoot().getObjectType(qname);
    }

    public static final com.arsdigita.persistence.proto.metadata.ObjectType type(ObjectType type) {
        return type(type.getQualifiedName());
    }

    public static final ObjectType fromType(com.arsdigita.persistence.proto.metadata.ObjectType type) {
        return MetadataRoot.getMetadataRoot().getObjectType(type.getQualifiedName());
    }

    public static final com.arsdigita.persistence.proto.metadata.Property prop(Property prop) {
        return type(prop.getContainer().getQualifiedName())
            .getProperty(prop.getName());
    }

    public static final PropertyMap pmap(OID oid) {
        com.arsdigita.persistence.proto.metadata.ObjectType type =
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
