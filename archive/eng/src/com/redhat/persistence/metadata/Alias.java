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
 * Alias
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Alias extends Property {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Alias.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Property m_target;

    public Alias(String name, Property target) {
        super(name);
        m_target = target;
    }

    public Property getTarget() {
        return m_target;
    }

    public ObjectType getType() {
        return m_target.getType();
    }

    public boolean isNullable() {
        return m_target.isNullable();
    }

    public boolean isCollection() {
        return m_target.isCollection();
    }

    public boolean isComponent() {
        return m_target.isComponent();
    }

    public boolean isComposite() {
        return m_target.isComposite();
    }

    public void dispatch(Switch sw) {
        sw.onAlias(this);
    }

}
