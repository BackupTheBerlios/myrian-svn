/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.metadata.ObjectType;

/**
 * A generic class to persist and an appropriate adapter.
 *
 * @author <a href="mailto:ashah@redhat.com">Archit Shah</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class Generic {

    private ObjectType m_type;
    private Object m_id;

    public static class Adapter
        extends com.redhat.persistence.metadata.Adapter {

        public Object getObject(ObjectType type, PropertyMap properties) {
            return new Generic(type, properties.get(type.getProperty("id")));
        }

        public PropertyMap getProperties(Object obj) {
            Generic g = (Generic) obj;
            PropertyMap result = new PropertyMap(g.getType());
            result.put(g.getType().getProperty("id"), g.getID());
            return result;
        }

        public ObjectType getObjectType(Object obj) {
            return ((Generic) obj).getType();
        }
    }

    public Generic(ObjectType type, Object id) {
        m_type = type;
        m_id = id;
    }

    public ObjectType getType() {
        return m_type;
    }

    public Object getID() {
        return m_id;
    }

    public String toString() {
        return m_type + ": " + m_id;
    }

    public int hashCode() {
        return m_id.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Generic) {
            Generic g = (Generic) o;
            return g.getID().equals(getID()) && g.getType().equals(getType());
        }

        return false;
    }
}
