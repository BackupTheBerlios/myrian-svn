package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * TypeException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/04/17 $
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
