/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import org.myrian.persistence.PropertyMap;
import org.myrian.persistence.metadata.Root;
import java.util.Iterator;
import java.util.Map;

/**
 * C
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

final class C {


    public static final org.myrian.persistence.metadata.ObjectType type
        (Root root, String qname) {
        return root.getObjectType(qname);
    }

    public static final org.myrian.persistence.metadata.ObjectType type
        (Root root, ObjectType type) {
        return type(root, type.getQualifiedName());
    }

    public static final ObjectType fromType
        (MetadataRoot root, org.myrian.persistence.metadata.ObjectType type) {
        return root.getObjectType(type.getQualifiedName());
    }

    public static final org.myrian.persistence.metadata.Property prop
        (Root root, Property prop) {
        return type(root, prop.getContainer().getQualifiedName())
            .getProperty(prop.getName());
    }

    public static final PropertyMap pmap(Root root, OID oid) {
        org.myrian.persistence.metadata.ObjectType type =
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
