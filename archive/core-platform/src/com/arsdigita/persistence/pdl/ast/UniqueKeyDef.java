/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.metadata.*;

import java.util.*;

/**
 * Defines a unique key for an ObjectType.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/08/14 $
 */

public class UniqueKeyDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/UniqueKeyDef.java#2 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    // the attributes that compose the key
    public List m_attrs = new ArrayList();

    public UniqueKeyDef() {}

    /**
     * Adds an attribute to the key
     *
     * @param attr the new attribute that makes up a part of the key
     */
    public void add(String attr) {
        m_attrs.add(attr);
    }

    /**
     * Create the metadata for this particular key
     *
     * @param type the type to add this key to
     */
    public void generateKey(ObjectType type) {
        Property[] props = new Property[m_attrs.size()];

        for (int i = 0; i < m_attrs.size(); i++) {
            String name = (String) m_attrs.get(i);
            props[i] = type.getProperty(name);
            if (props[i] == null) {
                error("No such property: " + name);
            }
        }

        if (type.hasUniqueKey(props)) {
            error("Duplicate key");
        } else {
            type.addUniqueKey(props);
        }
    }
}
