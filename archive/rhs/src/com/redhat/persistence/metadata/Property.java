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

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public abstract class Property extends Element {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/Property.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";


    public static abstract class Switch {
        public abstract void onRole(Role role);
        public abstract void onAlias(Alias alias);
        public abstract void onLink(Link link);
    }


    private String m_name;

    protected Property(String name) {
        m_name = name;
    }

    public Root getRoot() {
	return getContainer().getRoot();
    }

    public ObjectType getContainer() {
        return (ObjectType) getParent();
    }

    public String getName() {
        return m_name;
    }

    public boolean isKeyProperty() {
	return getContainer().isKeyProperty(this);
    }

    public abstract ObjectType getType();

    public abstract boolean isNullable();

    public abstract boolean isCollection();

    public abstract boolean isComponent();

    public abstract boolean isComposite();

    public abstract void dispatch(Switch sw);

    Object getElementKey() {
        return m_name;
    }

    public String toString() {
        return getName();
    }
}
