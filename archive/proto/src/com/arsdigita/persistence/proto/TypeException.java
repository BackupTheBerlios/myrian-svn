package com.arsdigita.persistence.proto;

/**
 * TypeException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/04/04 $
 **/

public class TypeException extends ProtoException {

    private Role m_role;

    TypeException(Role role) {
	m_role = role;
    }

    public Role getRole() {
	return m_role;
    }

}
