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

package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * PropertyMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/10/28 $
 **/

public class PropertyMap {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/PropertyMap.java#3 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private ObjectType m_type;
    private HashMap m_values = new HashMap();

    public PropertyMap(ObjectType type) {
        m_type = type;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public Object get(Property prop) {
        return m_values.get(prop);
    }

    public void put(Property prop, Object obj) {
        m_values.put(prop, obj);
    }

    public Set entrySet() {
        return m_values.entrySet();
    }

    boolean isNull() {
        Collection keys = m_type.getKeyProperties();
        if (keys.size() == 0) {
            return false;
        }

        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (get(prop) != null) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        return "<properties type=" + m_type + " values=" + m_values + ">";
    }

}
