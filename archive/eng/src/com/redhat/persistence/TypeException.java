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

import com.redhat.persistence.metadata.ObjectType;

/**
 * TypeException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class TypeException extends ProtoException {

    private Role m_role;
    private ObjectType m_expected;
    private ObjectType m_actual;
    private Object m_obj;

    TypeException(Role role, ObjectType expected, ObjectType actual,
                  Object obj) {
	m_role = role;
        m_expected = expected;
        m_actual = actual;
        m_obj = obj;
    }

    public Role getRole() {
	return m_role;
    }

    public String getMessage() {
        return m_role + " (" + m_obj + ") is of type " + m_actual
            + " instead of " + m_expected;
    }

}
