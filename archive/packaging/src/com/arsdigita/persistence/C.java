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
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

final class C {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/C.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

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
