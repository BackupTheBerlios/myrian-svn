/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.pdl.nodes;

import java.util.Collection;
import java.util.HashMap;

/**
 * EventNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/

public class EventNd extends Node {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/nodes/EventNd.java#2 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    private static final HashMap TYPES = new HashMap();

    public static final class Type {

        private String m_name;

        private Type(String name) {
            m_name = name;
            TYPES.put(m_name, this);
        }

        public String toString() {
            return m_name;
        }

    }

    public static final Type getType(String name) {
        return (Type) TYPES.get(name);
    }

    public static final Type INSERT = new Type("insert");
    public static final Type UPDATE = new Type("update");
    public static final Type DELETE = new Type("delete");
    public static final Type ADD = new Type("add");
    public static final Type REMOVE = new Type("remove");
    public static final Type CLEAR = new Type("clear");
    public static final Type RETRIEVE = new Type("retrieve");
    public static final Type RETRIEVE_ALL = new Type("all");
    public static final Type RETRIEVE_ATTRIBUTES = new Type("attributes");

    public static final Field SQL =
        new Field(EventNd.class, "sql", SQLBlockNd.class);
    public static final Field NAME =
        new Field(EventNd.class, "name", IdentifierNd.class, 0, 1);
    public static final Field SUPERS =
        new Field(EventNd.class, "supers", SuperNd.class);

    private Type m_type;

    public void setType(Type type) {
        m_type = type;
    }

    public Type getType() {
        return m_type;
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public Collection getSQL() {
        return (Collection) get(SQL);
    }

    public boolean isSingle() {
        return m_type.equals(RETRIEVE_ALL) ||
            (m_type.equals(RETRIEVE) && getName() != null);
    }

    public Collection getSupers() {
        return (Collection) get(SUPERS);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onEvent(this);
    }

}
