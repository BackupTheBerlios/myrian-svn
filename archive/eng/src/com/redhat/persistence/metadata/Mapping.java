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
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;

import java.util.*;

/**
 * Mapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/30 $
 **/

public abstract class Mapping extends Element {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Mapping.java#4 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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

    public Property getProperty () {
        return getObjectMap().getObjectType().getProperty(m_path);
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
        setMap(map, true);
    }

    public void setMap(ObjectMap map, boolean nest) {
        if (nest && map != null && map.getParent() == null) {
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

    public Mapping reverse(final Path p) {
        final Mapping[] result = { null };
        dispatch(new Mapping.Switch() {
            public void onJoinTo(JoinTo j) {
                result[0] = new JoinFrom(p, j.getKey());
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] = new JoinTo(p, j.getKey());
            }
            public void onJoinThrough(JoinThrough j) {
                result[0] = new JoinThrough(p, j.getTo(), j.getFrom());
            }
            public void onQualias(Qualias q) {
                String qname = q.getObjectMap().getObjectType()
                    .getQualifiedName();
                result[0] = new Qualias
                    (p, "filter(all(" + qname +
                     "), exists(filter(that = " + q.getPath().getPath() +
                     ", this == that)))");
            }
            public void onValue(Value v) {
                throw new IllegalArgumentException("not reversible: " + v);
            }
            public void onStatic(Static s) {
                result[0] = new Static(p);
            }
            public void onNested(Nested n) {
                // XXX: kill nested
                result[0] = new Static(p);
            }
        });
        return result[0];
    }

    public abstract void dispatch(Switch sw);

    Object getElementKey() {
        return getPath();
    }

    public String toString() {
        return "<mapping for " + m_path + ">";
    }

}
