package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.metadata.Root;

/**
 * C
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/09 $
 **/

final class C {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/C.java#1 $ by $Author: rhs $, $DateTime: 2003/01/09 18:21:44 $";

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

}
