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

package com.redhat.persistence.metadata;

/**
 * Role
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class Role extends Property {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/Role.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

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
