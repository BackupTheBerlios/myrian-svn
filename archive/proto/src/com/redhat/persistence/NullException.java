package com.redhat.persistence;

/**
 * NullException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class NullException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/NullException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Role m_role;

    NullException(Role role) {
	m_role = role;
    }

    public Role getRole() {
	return m_role;
    }

}
