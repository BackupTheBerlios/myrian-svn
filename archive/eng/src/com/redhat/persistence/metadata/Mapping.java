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
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;

import java.util.*;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/05 $
 **/

public abstract class Mapping extends Element {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Mapping.java#2 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    public static abstract class Switch {
        public abstract void onValue(Value m);
        public abstract void onJoinTo(JoinTo m);
        public abstract void onJoinFrom(JoinFrom m);
        public abstract void onJoinThrough(JoinThrough m);
        public abstract void onStatic(Static m);
        public void onQualias(Qualias q) {
            throw new UnsupportedOperationException();
        }
        public abstract void onNested(Nested n);
    }

    private Path m_path;
    private ObjectMap m_map;
    private SQLBlock m_retrieve;
    private ArrayList m_adds = null;
    private ArrayList m_removes = null;

    protected Mapping(Path path, ObjectMap map) {
        m_path = path;
        setMap(map);
    }

    protected Mapping(Path path) {
        this(path, null);
    }

    public ObjectMap getObjectMap() {
        return (ObjectMap) getParent();
    }

    public Path getPath() {
        return m_path;
    }

    public boolean isNested() {
        return getMap().isNested();
    }

    public boolean isCompound() {
        return getMap().isCompound();
    }

    public boolean isPrimitive() {
        return getMap().isPrimitive();
    }

    public void setMap(ObjectMap map) {
        if (map != null && map.getParent() == null) {
            map.setParent(this);
        }
        m_map = map;
    }

    public ObjectMap getMap() {
        if (m_map == null) {
            ObjectMap om = getObjectMap();
            ObjectType type = om.getObjectType();
            Root root = om.getRoot();
            return root.getObjectMap(type.getProperty(m_path).getType());
        } else {
            return m_map;
        }
    }

    public abstract List getColumns();

    public abstract Table getTable();

    public SQLBlock getRetrieve() {
        return m_retrieve;
    }

    public void setRetrieve(SQLBlock retrieve) {
        m_retrieve = retrieve;
    }

    public Collection getAdds() {
        return m_adds;
    }

    public void setAdds(Collection adds) {
        if (adds == null) {
            m_adds = null;
        } else {
            m_adds = new ArrayList();
            m_adds.addAll(adds);
        }
    }

    public Collection getRemoves() {
        return m_removes;
    }

    public void setRemoves(Collection removes) {
        if (removes == null) {
            m_removes = null;
        } else {
            m_removes = new ArrayList();
            m_removes.addAll(removes);
        }
    }

    public abstract void dispatch(Switch sw);

    Object getElementKey() {
        return getPath();
    }

    public String toString() {
        return "<mapping for " + m_path + ">";
    }

}
