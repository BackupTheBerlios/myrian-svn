/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.metadata.Root;
import java.util.Iterator;
import java.util.Map;

/**
 * C
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

final class C {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/C.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
