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

package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;

/**
 * TypeException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
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
