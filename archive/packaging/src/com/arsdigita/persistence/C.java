/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.PropertyMap;
import java.util.*;

/**
 * C
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/09/10 $
 **/

final class C {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/C.java#4 $ by $Author: rhs $, $DateTime: 2003/09/10 10:46:29 $";

    public static final com.redhat.persistence.metadata.ObjectType type
        (Root root, String qname) {
        return root.getObjectType(qname);
    }

    public static final com.redhat.persistence.metadata.ObjectType type
        (Root root, ObjectType type) {
        return type(root, type.getQualifiedName());
    }

    public static final ObjectType fromType
        (MetadataRoot root, com.redhat.persistence.metadata.ObjectType type) {
        return root.getObjectType(type.getQualifiedName());
    }

    public static final com.redhat.persistence.metadata.Property prop
        (Root root, Property prop) {
        return type(root, prop.getContainer().getQualifiedName())
            .getProperty(prop.getName());
    }

    public static final PropertyMap pmap(Root root, OID oid) {
        com.redhat.persistence.metadata.ObjectType type =
            type(root, oid.getObjectType());
        PropertyMap result = new PropertyMap(type);
        for (Iterator it = oid.getProperties().entrySet().iterator();
             it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            result.put(type.getProperty((String) me.getKey()), me.getValue());
        }

        return result;
    }

}
