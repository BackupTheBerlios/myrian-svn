package com.redhat.persistence;

/**
 * NullException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class NullException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/NullException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Role m_role;

    NullException(Role role) {
	m_role = role;
    }

    public Role getRole() {
	return m_role;
    }

}
