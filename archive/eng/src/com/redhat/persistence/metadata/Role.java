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

/**
 * Role
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Role extends Property {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Role.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private ObjectType m_type;
    private Role m_reverse;
    private boolean m_isComponent;
    private boolean m_isCollection;
    private boolean m_isNullable;

    public Role(String name, ObjectType type, boolean isComponent,
                boolean isCollection, boolean isNullable) {
        super(name);
        m_type = type;
        m_isComponent = isComponent;
        m_isCollection = isCollection;
        m_isNullable = isNullable;
    }

    public ObjectType getType() {
        return m_type;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isComposite() {
        return m_reverse != null && m_reverse.isComponent();
    }

    public boolean isNullable() {
        return m_isNullable;
    }

    public void setNullable(boolean value) {
        m_isNullable = value;
    }

    public boolean isCollection() {
        return m_isCollection;
    }

    public boolean isReversable() {
        return m_reverse != null;
    }

    public Role getReverse() {
        return m_reverse;
    }

    public void setReverse(Role reverse) {
        if (reverse == null) {
            throw new IllegalArgumentException
                ("reverse is null");
        }
        if (reverse.m_reverse != null) {
            throw new IllegalArgumentException
                ("Role is associated with another property already: " +
                 reverse);
        }

        this.m_reverse = reverse;
        reverse.m_reverse = this;
    }

    public void dispatch(Switch sw) {
        sw.onRole(this);
    }

}
