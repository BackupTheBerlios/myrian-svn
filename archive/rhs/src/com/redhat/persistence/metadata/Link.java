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
package com.redhat.persistence.metadata;



/**
 * Link
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/

public class Link extends Property {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/Link.java#2 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    private Role m_from;
    private Role m_to;
    private boolean m_isCollection;
    private boolean m_isNullable;

    public Link(String name, Role from, Role to, boolean isCollection,
		boolean isNullable) {
        super(name);
        m_from = from;
	m_to = to;
	m_isCollection = isCollection;
	m_isNullable = isNullable;
    }

    public ObjectType getType() {
        return m_to.getType();
    }

    public boolean isCollection() {
        return m_isCollection;
    }

    public boolean isNullable() {
        return m_isNullable;
    }

    public boolean isComponent() {
        return m_to.isComponent();
    }

    public boolean isComposite() {
        return m_to.isComposite();
    }

    public Role getFrom() {
	return m_from;
    }

    public Role getTo() {
	return m_to;
    }

    public ObjectType getLinkType() {
	return m_from.getContainer();
    }

    public void dispatch(Switch sw) {
        sw.onLink(this);
    }

}
