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
package com.redhat.persistence;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.metadata.ObjectType;

/**
 * A generic class to persist and an appropriate adapter.
 *
 * @author <a href="mailto:ashah@redhat.com">Archit Shah</a>
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class Generic {

    private ObjectType m_type;
    private Object m_id;

    public static class Adapter
        extends com.redhat.persistence.metadata.Adapter {

        public Object getObject(ObjectType type,
                                PropertyMap properties,
                                Session ssn) {
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
